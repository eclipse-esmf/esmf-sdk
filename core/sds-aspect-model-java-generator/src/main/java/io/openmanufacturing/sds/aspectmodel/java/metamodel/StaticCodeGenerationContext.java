package io.openmanufacturing.sds.aspectmodel.java.metamodel;

import io.openmanufacturing.sds.aspectmodel.java.JavaCodeGenerationConfig;
import io.openmanufacturing.sds.metamodel.Characteristic;

@lombok.Value
@lombok.With
public class StaticCodeGenerationContext {
   JavaCodeGenerationConfig codeGenerationConfig;
   String modelUrnPrefix;
   String characteristicBaseUrn;
   Characteristic currentCharacteristic;
}
