# AAS Generator Module of the BAMM SDK

The Asset Administration Shell (AAS) and its information model [1] is a widely recognized standard developed by the 
Industrial Digital Twin Association (IDTA) [2] to express and handle Digital Twins. Central element of the AAS is 
the concept of Submodels, which describe certain aspects of a Digital Twin.

The BAMM Aspect Meta Model allows to specify aspects of a digital twin and its semantics. 
The AAS Generator module provides mapping implementations to derive AAS Submodels from BAMM Aspect models
and by that allows to on the one hand integrate BAMM models in AAS environments and on the other hand allow 
AAS Submodels to be described with rich semantics, as it is possible with BAMM.

The implementation relies on the AAS Meta Model implementation [3] and the AAS file serializers provided by [4].



 

## Example uses
One example use of the generator is with the BAMM CLI, which provides the AAS generator functionality by
```
java -jar bamm-cli.jar -i org.idtwin/1.0.0/Nameplate.ttl -aas-xml
```
or
```
java -jar bamm-cli.jar -i org.idtwin/1.0.0/Nameplate.ttl -aas-aasx
```
The first call generates a plain xml represenation of the AAS whereas the second one generates an AASX archive
with the AAS xml file in it.

Should the generator be integrated into custom implementations, the class ``AspectModelAASGenerator``
with its method ``io.openmanufacturing.sds.aspectmodel.aas.AspectModelAASGenerator.generateOutput`` is the 
propery entry point. It expects an ``io.openmanufacturing.sds.metamodel.Aspect`` and returns a 
``ByteArrayOutputStream``.


## Details to the Mapping Concept
In the following section, the mapping rules applied by the generator are explained. 
The rules apply to BAMM v1.0.0 [6] and  AAS Specification Part 1 V3.0RC01 [7].

| BAMM  | AAS  | Comment  |
|---    |---   |---       |
| **bamm:Aspect**  |  aas:Submodel with kind=Template  | Empty Asset and AssetAdministrationShell entries are added to the output file  |
| bamm:name   | aas:Submodel.idShort  |       |
| bamm:preferredName   | aas:Submodel.displayName  |       |
| bamm:description   | aas:Submodel.description  |       |
| bamm:property   | see **bamm:Property**  |       |
| bamm:operation   | see **bamm:Operation**   |       |
| bamm:Aspect.urn  | aas:Submodel.semanticId  |       |
| **Property**  | aas:Property <br> aas:SubmodelElementCollection   | The AAS type is derived from the type of the BAMM Characteristic specifying the BAMM property. Depending on the type it is decided what the resulting AAS element will be. In case of an Entity it will result in a SubmodelElementCollection. It will also be a SubmodelElementCollection if the BAMM Characteristic is of a Collection type (see the Characteristics taxonomy [5]). In all other cases an aas:Property will be generated   |
| bamm:Property.name  | aas:Property.idShort |       |
| bamm:Property.urn  | aas:ConceptDescription.identification.id <br> aas:Property.semanticId|       |
| bamm:Property.preferredName   | aas:Property.displayName  |       |
| bamm:Property.description   | aas:Property.description  |  Note: Also mapped to aas:DataSpecificationIEC61360.definition of the aas:ConceptDescription of this property     |
| bamm:Property.exampleValue  |aas:Property.value  |       |
| bamm:Characteristic.dataType  | aas:Property.valueType |       |
| **Operation** |  Operation  | in/out parameters are not used in BAMM so the mapping only generates input variables and output variables in AAS |
| **Characteristic**  | aas:SubmodelElement, aas:ConceptDescription  | Characteristics in BAMM define the semantics of a property, which includes there types as well as links to further definitions (standards, dictionaries, etc), a natural language description and name in different languages. Type and description are separated in AAS, which is why there is not a one-to-one mapping of a Characteristic to one element in AAS but rather Characteristics are used in the mapping of Properties, first, to guide the generation process and, second, to capture semantics in ConceptDescriptions of properties with data specification "DataSpecificationIEC61360" of the AAS.    |
| **Collection**  | aas:SubmodelElementCollection, aas:ConceptDescription  | The general remarks to Characteristics apply also for Collection type Characteristics. However, properties referencing Collections are mapped to SubmodelElementCollections. Specific properties of collections are mapped. bamm:Set is unique, bamm:SortedSet is unique and sorted, bamm:List is sorted.       |
| **Quantifiable**  | aas:SubmodelElement, aas:ConceptDescription | The general remarks to Characteristics apply also for Quantifiable type Characteristics. Quantifiables (also Duration and Measurement) reference a unit, which is added to the ConceptDescription corresponding the mapped Characteristic.       |
| **Either**  | aas:SubmodelElement, aas:ConceptDescription | The general remarks to Characteristics apply also for Either. However, the Either characteristic has two distinct entries of which one is to be selected. This concept is not present in AAS. Thus both entries will be written to a Submodel template, where one has to be ignored.       |
| **Trait**  | aas:SubmodelElement, aas:ConceptDescription | The general remarks to Characteristics apply also for Trait. However, the constraint of a trait will be ignored and only the base type will be evaluated, which will act as the characteristic of a property. |
| **Code**  | aas:SubmodelElement, aas:ConceptDescription | Similar to plain Characteristic. |
| **StructuredValue**  | aas:SubmodelElement, aas:ConceptDescription | The general remarks to Characteristics apply also for StructuredValue. However, AAS has no concpet like deconstruction rule. Thus, the deconstruction rule and the sub properties of the deconstruction entity will be ignored and only the Characteristic is mapped. |
| **Enumeration**  | aas:SubmodelElement, aas:ConceptDescription | The general remarks to Characteristics apply also for Enumerations. Additionally, the values of an Enumeration are mapped to a valueList of a DataSpecificationIEC61360. |
| **State**  | aas:SubmodelElement, aas:ConceptDescription | Same as Enumeration. |
| bamm:MultiLanguageString  | aas:MultiLanguageProperty | if a multilanguage string is used in BAMM it is mapped to the MultiLanguageProperty in AAS. |


## Known Limitations
The AAS generator implements a base set of features, which are mapped from BAMM to AAS. 
However, there are still limitations:
* Predefined entity mapping (FileResource would be mapped to aas:File)
* BAMM:Either is mapped to aas:SubmodelElementCollection with two entries for left and right side
* Recursive optional properties of BAMM model are not included in output but dropped straight away


## References
[1] https://www.plattform-i40.de/IP/Redaktion/EN/Standardartikel/specification-administrationshell.html

[2] https://industrialdigitaltwin.org

[3] https://github.com/admin-shell-io/java-model

[4] https://github.com/admin-shell-io/java-serializer

[5] https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/snapshot/characteristics.html

[6] https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/v1.0.0/index.html

[7] https://www.plattform-i40.de/IP/Redaktion/EN/Downloads/Publikation/Details_of_the_Asset_Administration_Shell_Part1_V3.html
