package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.Identifiable;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.openmanufacturing.sds.metamodel.Property;

import java.util.HashMap;
import java.util.Map;

public class Context {

    AssetAdministrationShellEnvironment environment;
    Submodel submodel;
    Property property;
    SubmodelElement propertyResult;

    public Context(AssetAdministrationShellEnvironment environment, Submodel ofInterest) {
        this.environment = environment;
        this.submodel = ofInterest;
    }

    public AssetAdministrationShellEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(AssetAdministrationShellEnvironment environment) {
        this.environment = environment;
    }

    public Submodel getSubmodel() {
        return submodel;
    }

    public void setSubmodel(Submodel submodel) {
        this.submodel = submodel;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public SubmodelElement getPropertyResult() {
        return propertyResult;
    }

    public void setPropertyResult(SubmodelElement propertyResult) {
        this.propertyResult = propertyResult;
    }

}
