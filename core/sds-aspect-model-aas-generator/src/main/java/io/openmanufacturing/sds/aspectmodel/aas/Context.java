package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.Identifiable;

public class Context {
    public Context(AssetAdministrationShellEnvironment environment, Identifiable ofInterest) {
        this.environment = environment;
        this.ofInterest = ofInterest;
    }

    public AssetAdministrationShellEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(AssetAdministrationShellEnvironment environment) {
        this.environment = environment;
    }

    public Identifiable getOfInterest() {
        return ofInterest;
    }

    public void setOfInterest(Identifiable ofInterest) {
        this.ofInterest = ofInterest;
    }

    AssetAdministrationShellEnvironment environment;
    Identifiable ofInterest;
}
