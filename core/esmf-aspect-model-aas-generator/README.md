# AAS Generator Module of the ESMF SDK

The Asset Administration Shell (AAS) and its information model [1] is a widely recognized standard developed by the 
Industrial Digital Twin Association (IDTA) [2] to express and handle Digital Twins. Central element of the AAS is 
the concept of Submodels, which describe certain aspects of a Digital Twin.

The Semantic Aspect Meta Model allows to specify aspects of a digital twin and its semantics. 
The AAS Generator module provides mapping implementations to derive AAS Submodels from SAMM Aspect Models
and by that allows to on the one hand integrate SAMM models in AAS environments and on the other hand allow 
AAS Submodels to be described with rich semantics, as it is possible with SAMM.

The implementation relies on the AAS Meta Model implementation [3] and the AAS file serializers provided by [4].

## Example uses
One example use of the generator is with the SAMM CLI, which provides the AAS generator functionality by
```
java -jar samm-cli.jar aspect org.idtwin/1.0.0/Nameplate.ttl to aas -f xml -o nameplate.xml
```
or
```
java -jar samm-cli.jar aspect org.idtwin/1.0.0/Nameplate.ttl to aas -f aasx -o nameplate.aasx
```
The first call generates a plain xml representation of the AAS whereas the second one generates an AASX archive
with the AAS xml file in it.
 
Should the generator be integrated into custom implementations, the class `AspectModelAASGenerator`
with its method `org.eclipse.esmf.aspectmodel.aas.AspectModelAasGenerator.generateOutput` is the 
proper entry point. It expects an `org.eclipse.esmf.metamodel.Aspect` and returns a 
`ByteArrayOutputStream`.


## Details of the Mapping Concept
The mapping rules applied by the generator are explained in the [user documentation](https://eclipse-esmf.github.io/esmf-developer-guide/tooling-guide/java-aspect-tooling.html#mapping-aas).
The rules apply to SAMM v2.0.0 [5] and  AAS Specification Part 1 V3.0RC01 [6].


## References
[1] https://www.plattform-i40.de/IP/Redaktion/EN/Standardartikel/specification-administrationshell.html

[2] https://industrialdigitaltwin.org

[3] https://github.com/admin-shell-io/java-model

[4] https://github.com/admin-shell-io/java-serializer

[5] https://eclipse-esmf.github.io/samm-specification/2.0.0/index.html

[6] https://www.plattform-i40.de/IP/Redaktion/EN/Downloads/Publikation/Details_of_the_Asset_Administration_Shell_Part1_V3.html
