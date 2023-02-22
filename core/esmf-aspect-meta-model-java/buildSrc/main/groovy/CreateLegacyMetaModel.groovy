/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */


import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy
import com.github.javaparser.utils.SourceRoot

import java.nio.file.Paths
import java.util.function.Consumer

class Utils {
    static JAVA_EXT = ".java"

    static String withoutExtension(File file) {
        return file.name.replace(JAVA_EXT, "")
    }

    static boolean isOpenmanufacturing(Object node) {
        return node.toString().startsWith("io.openmanufacturing")
    }
}

metaModelRelativePath = "io/openmanufacturing/sds/metamodel"
destinationPath = new File("${project.basedir}/../sds-aspect-meta-model-legacy/src/main/java/")
VERSION = "${project.version}"
POSTFIX = "_" + VERSION.replaceAll("[.\\-]", "_")

processSourceDirectories([
        new CopySource("${project.basedir}/src/main/java/", metaModelRelativePath,
                ["metamodel", "datatypes", "impl", "loader", "visitor", "BAMM", "BAMMC"]),
        new CopySource("${project.basedir}/src-gen/main/java/", metaModelRelativePath,
                ["Unit", "QuantityKind"])],
        POSTFIX, destinationPath)

/**
 * Processes all source directories to be used for the legacy meta model package and applies the respective refactorings.
 *
 * @param sources the sources to be used for the legacy meta model package
 * @param version the target version to be applied for renaming
 * @param destinationPath the destination path where the legacy meta model should be written to
 */
void processSourceDirectories(List<CopySource> sources, String version, File destinationPath) {
    destinationPath.deleteDir()
    destinationPath.mkdirs()

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver(false));
    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

    def projectRoot = new SymbolSolverCollectionStrategy().collect(Paths.get("${project.basedir}"))

    List<String> toRename = sources.collectMany { it.collectFilesToRename() }
    sources.each { source ->
        SourceRoot sourceFiles = projectRoot.getSourceRoots().find { it.getRoot().startsWith(source.getBaseDir()) }
        def parseResult = sourceFiles.tryToParse()
        def resolverVisitor = new ResolverVisitor()
        sourceFiles.getCompilationUnits().each { unit -> resolverVisitor.visit(unit, null) }
        def visitor = new RenamingVisitor(toRename, version, sourceFiles, resolverVisitor)
        sourceFiles.getCompilationUnits().each { unit -> visitor.visit(unit, null) }

        sourceFiles.saveAll(destinationPath.toPath())
        destinationPath.eachFileRecurse { file ->
            if (file.isFile() && toRename.contains(Utils.withoutExtension(file))) {
                file.renameTo(new File(file.getParent(), Utils.withoutExtension(file) + version + Utils.JAVA_EXT))
            }
        }
    }
}

/**
 * Defines a source directory from which meta model classes shall be copied to the legacy meta model package.
 */
class CopySource {
    private final String baseDir

    private final String relativeSourcePath

    private final List<String> include

    CopySource(final String baseDir, final String relativeSourcePath, final List<String> include) {
        this.baseDir = baseDir
        this.relativeSourcePath = relativeSourcePath
        this.include = include
    }

    String getBaseDir() {
        return baseDir
    }

    List<String> collectFilesToRename() {
        List<String> collectedFileNames = []
        def sourcePath = new File(baseDir + relativeSourcePath)
        sourcePath.eachFileRecurse { file ->
            if (file.isFile() && (include.contains(Utils.withoutExtension(file)) ||
                    include.any { file.parentFile.toPath().endsWith(it) })) {
                collectedFileNames.add(Utils.withoutExtension(file))
            }
        }
        return collectedFileNames
    }
}

/**
 * A visitor that performs the actual refactoring of the meta model classes so that they are postfixed with the
 * legacy version for which the package is generated.
 */
class RenamingVisitor extends VoidVisitorAdapter<Void> {

    private final List<String> toRename

    private final String version

    private final SourceRoot sourceRoot

    private final ResolverVisitor resolverVisitor

    private final Map<String, Map<String, String>> imports = new HashMap<>()

    RenamingVisitor(List<String> toRename, String version, SourceRoot sourceRoot, ResolverVisitor resolverVisitor) {
        this.toRename = toRename
        this.version = version
        this.sourceRoot = sourceRoot
        this.resolverVisitor = resolverVisitor
    }

    @Override
    void visit(ClassOrInterfaceDeclaration n, Void arg) {
        findExactMatch(n.getName().toString()).ifPresent({ match ->
            n.setName(match + version)
        } as Consumer)
        super.visit(n, arg)
    }

    @Override
    void visit(EnumDeclaration n, Void arg) {
        findExactMatch(n.getName().toString()).ifPresent({ match ->
            n.setName(match + version)
        } as Consumer)
        super.visit(n, arg)
    }

    @Override
    void visit(ClassOrInterfaceType n, Void arg) {
        if (scopeIsOpenmanufacturing(n)) {
            findExactMatch(n.getName().toString()).ifPresent({ match ->
                n.setName(match + version)
            } as Consumer)
        }
        super.visit(n, arg)
    }

    private boolean scopeIsOpenmanufacturing(ClassOrInterfaceType n) {
        if (n.getScope().isPresent()) {
            return Utils.isOpenmanufacturing(n.getScope().get())
        }
        return typeIsOpenmanufacturing(n)
    }

    private Boolean typeIsOpenmanufacturing(Node n) {
        Map<String, String> importsOfFile = imports.get(getCompilationUnitName(n))
        if (importsOfFile != null) {
            return importsOfFile.containsKey(n.getNameAsString()) ? Utils.isOpenmanufacturing(importsOfFile.get(n.getNameAsString())) : true
        }
        ResolvedReferenceType resolvedType = null
        try {
            resolvedType = n.resolve()
        } catch (Exception e) {
            // do nothing, continue with further resolution methods
        }
        if (resolvedType == null) {
            resolvedType = resolverVisitor.tryGetResolvedType(n.getNameAsString()).orElse(null)
        }
        return resolvedType != null ? Utils.isOpenmanufacturing(resolvedType.getTypeDeclaration().getQualifiedName()) : false
    }

    private String getCompilationUnitName(Node n) {
        Optional<Node> parent = n.getParentNode()
        while (parent.isPresent() && !(parent.get() instanceof CompilationUnit)) {
            parent = parent.get().getParentNode()
        }
        return parent.get().getPrimaryTypeName().get()
    }

    @Override
    void visit(ImportDeclaration n, Void arg) {
        def className = n.getParentNode().get().getPrimaryTypeName().get()
        def importsOfClass = imports.get(className)
        if (importsOfClass == null) {
            importsOfClass = new HashMap<String, String>()
            imports.put(className, importsOfClass)
        }
        importsOfClass.put(n.getName().getIdentifier(), n.getName().toString())

        if (Utils.isOpenmanufacturing(n.getName())) {
            findEndsWithMatch(n.getName().toString()).ifPresent({ match ->
                if (!n.getName().toString().endsWith("DataType"))
                    n.setName(n.getName().toString().replace(match, match + version))
            } as Consumer)
        }
        super.visit(n, arg)
    }

    @Override
    void visit(ConstructorDeclaration n, Void arg) {
        findExactMatch(n.getName().toString()).ifPresent({ match ->
            n.setName(match + version)
        } as Consumer)
        super.visit(n, arg)
    }

    @Override
    void visit(NameExpr n, Void arg) {
        if (typeIsOpenmanufacturing(n)) {
            findExactMatch(n.getName().toString()).ifPresent({ match ->
                n.setName(match + version)
            } as Consumer)
        }
        super.visit(n, arg);
    }

    Optional<String> findExactMatch(final String elementName) {
        return Optional.ofNullable(toRename.find { it.equals(elementName) })
    }

    Optional<String> findEndsWithMatch(final String elementName) {
        return Optional.ofNullable(toRename.find { elementName.endsWith(it) })
    }
}

/**
 * A visitor to be applied to the AST of all Java files that are part of the refactoring in order to resolve types.
 * This information then can be used during the refactoring to disambiguate classes from the meta model that have the
 * same name than a class from core Java (or anything else).
 */
class ResolverVisitor extends VoidVisitorAdapter<Void> {
    private Map<String, ResolvedReferenceType> resolvedTypes = new HashMap<>()

    Optional<ResolvedReferenceType> tryGetResolvedType(final String name) {
        return Optional.ofNullable(resolvedTypes.get(name))
    }

    @Override
    void visit(ClassOrInterfaceType n, Void arg) {
        try {
            resolvedTypes.put(n.getName().toString(), n.resolve())
        } catch (Exception e) {
            // do nothing
        }
        super.visit(n, arg)
    }
}

