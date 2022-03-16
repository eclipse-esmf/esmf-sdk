package io.openmanufacturing.sds.aspectmodel.aas;

import java.util.List;

import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.ConceptDescription;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.openmanufacturing.sds.metamodel.Property;

public class Context {

   AssetAdministrationShellEnvironment environment;
   Submodel submodel;
   Property property;
   SubmodelElement propertyResult;

   public Context( AssetAdministrationShellEnvironment environment, Submodel ofInterest ) {
      this.environment = environment;
      this.submodel = ofInterest;
   }

   public boolean hasEnvironmentConceptDescription( String name ) {
      return getEnvironment().getConceptDescriptions().stream().anyMatch( x -> x.getIdShort() == name );
   }

   public ConceptDescription getConceptDescription( String name ) {
      return getEnvironment().getConceptDescriptions().stream().filter( x -> x.getIdentification().getIdentifier().equals( name ) ).findFirst().get();
   }

   public AssetAdministrationShellEnvironment getEnvironment() {
      return environment;
   }

   public Submodel getSubmodel() {
      return submodel;
   }

   public void appendToSubModelElements( List<SubmodelElement> elements ) {
      // Hint: As the AAS Meta Model Implementation exposes the internal data structure where the elements
      // of a collection are stored, just setting it would overwrite previous entries. Hence this approach.
      getSubmodel().getSubmodelElements().addAll( elements );
   }

   public Property getProperty() {
      return property;
   }

   public void setProperty( Property property ) {
      this.property = property;
   }

   public SubmodelElement getPropertyResult() {
      return propertyResult;
   }

   public void setPropertyResult( SubmodelElement propertyResult ) {
      this.propertyResult = propertyResult;
   }

}
