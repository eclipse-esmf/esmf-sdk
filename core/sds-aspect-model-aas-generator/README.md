# AAS Generator Module of the BAMM SDK

The Asset Administration Shell (AAS) and its information model [1] is a widely recognized standard developed by the 
Industrial Digital Twin Association (IDTA) [2] to express and handle Digital Twins. Central element of the AAS is 
the concept of Submodels, which describe certain aspects of a Digital Twin.

The BAMM Aspect Meta Model allows to specify aspects of a digital twin and its semantics. 
The AAS Generator module provides mapping implementations to derive AAS Submodels from BAMM Aspect models
and by that allows to on the one hand integrate BAMM models in AAS environments and on the other hand allow 
AAS Submodels to be described with rich semantics, as it is allowed by BAMM.

The implementation relies on the AAS Meta Model implementation [3] and the AAS file serializers provided by [4].



 

## Example uses
One example use of the generator is with the BAMM CLI, which provides the AAS generator functionality by
```
java -jar bamm-cli.jar -i org.idtwin/1.0.0/Nameplate.ttl -aas-xml
```

Should the generator be integrated into custom implementations, the class ``AspectModelAASGenerator``
with its method ``io.openmanufacturing.sds.aspectmodel.aas.AspectModelAASGenerator.generateOutput`` is the 
propery entry point. It expects an ``io.openmanufacturing.sds.metamodel.Aspect`` and returns a 
``ByteArrayOutputStream``.


## Details to the Mapping Concept

In the following section, the mapping rules applied by the generator are explained.

| BAMM  | AAS  | Comment  |
|---    |---   |---       |
| **bamm:Aspect**  |  Submodel template  | Empty Asset and AssetAdministrationShell entries are added to the output file  |
| bamm:name   | aas:Submodel.idShort  |       |
| bamm:preferedName   | aas:Submodel.displayName  |       |
| bamm:description   | aas:Submodel.description  |       |
| bamm:property   | see **bamm:Property**  |       |
| bamm:operation   | see**bamm:Operation**   |       |
| bamm:Aspect.urn  | aas:Submodel.semanticId  |       |
| **Property**  | SubmodelElement <br> SubmodelElementCollection   | The AAS type is derived from the type of the BAMM Characteristic specifying the BAMM property. Depending on the type it is decided what the resulting AAS element will be. In case of an Entity it will result in a SubmodelElementCollection. It will also be a SubmodelElementCollection if the BAMM Characteristic is of a Collection type (see the Characteristics taxonomy [5])   |
| bamm:name  | aas:Property.idShort |       |
| bamm:preferedName   | aas:Property.displayName  |       |
| bamm:description   | aas:Property.description  |       |
| bamm:exampleValue  |aas:Property.value  |       |
| bamm:Characteristic.dataType  | aas:Property.valueType |       |
| **Operation** |  Operation  | in/out parameters are not used in BAMM so the mapping only generates input variables and output variables in AAS |
| **Characteristic**  | aas:ConceptDescription  | Characteristics in BAMM define the semantics of a property, which includes there types as well as links to further definitions (standards, dictionaries, etc) and a natural language description and name in different languages. Type and description are separated in AAS, which is why there is not a one-to-one mapping of a Characteristic to one element in AAS but rather Characteristics are used in the mapping of Properties to get additional information, which guides the generation process. The remaining semantics are then mapped as good as possible to a ConceptDescription.    |
| **Collection**  | aas:SubmodelElementCollection  | The general remarks to Characteristics apply also for Collection type Characteristics. However, properties referencing Collections are mapped to SubmodelElementCollections. Specific properties of collections are mapped. bamm:Set is unique, bamm:SortedSet is unique and sorted, bamm:List is sorted.       |
| **Quantifiable**  | aas:SubmodelElement | The general remarks to Characteristics apply also for Quantifiable type Characteristics. Quantifiables (also Duration and Measurement) reference a unit, which is added to the ConceptDescription corresponding the mapped Characteristic.       |
| **Either**  | aas:SubmodelElement | The general remarks to Characteristics apply also for Either. However, the Either characteristic has two distinct entries of which one is to be selected. This concept is not present in AAS. Thus both entries will be written to a Submodel template, where one has to be ignored.       |
| **Trait**  | aas:SubmodelElement | The general remarks to Characteristics apply also for Trait. However, the constraint of a trait will be ignored and only the base type will be evaluated, which will act as the characteristic of a property. |
| **Code**  | aas:SubmodelElement | Similar to plain Characteristic. |
| **StructuredValue**  | aas:SubmodelElement | The general remarks to Characteristics apply also for StructuredValue. However, AAS has no concpet like deconstruction rule. Thus, the deconstruction rule and the sub properties of the deconstruction entity will be ignored and only the Characteristic is mapped. |

| bamm:MultiLanguageProperty  | aas:LangString | if a multilanguage property is used in BAMM it is mapped to the lang string concept in AAS. |

## References
[1] https://www.plattform-i40.de/IP/Redaktion/EN/Standardartikel/specification-administrationshell.html

[2] https://industrialdigitaltwin.org

[3] https://github.com/admin-shell-io/java-model

[4] https://github.com/admin-shell-io/java-serializer

[5] https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/snapshot/characteristics.html
