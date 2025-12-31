package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<ThingModelProperty> properties;

    public Map<String, ThingModelProperty> thingModel2Map() {
        //物理模型属性List 2 Map
        Map<String, ThingModelProperty> thingModelPropertyMap = new HashMap<>();
        for (ThingModelProperty property : this.getProperties()) {
            thingModelPropertyMap.put(property.getIdentifier(), property);
        }
        return thingModelPropertyMap;
    }

    /**
     * 获取 identifier set
     *
     * @return
     */
    public Set<String> identifierSet() {
        Set<String> set = new HashSet<>();
        for (ThingModelProperty property : this.getProperties()) {
            set.add(property.getIdentifier());
        }
        return set;
    }

    public static Boolean thingModelPropertyCopy(ThingModel source, ThingModel target) {
        Map<String, ThingModelProperty> mapTarget = target.thingModel2Map();
        List<ThingModelProperty> changeThingModelProperties = source.getProperties();
        boolean flag = false;
        for (ThingModelProperty changeProperty : changeThingModelProperties) {
            ThingModelProperty thingModelProperty = mapTarget.get(changeProperty.getIdentifier());
            if (thingModelProperty != null) {
                thingModelProperty.setValue(changeProperty.getValue());
                flag = true;
            }
        }
        return flag;
    }

    public void thingModelPropertyExtend(ThingModel source) {
        List<ThingModelProperty> targetProperties = this.getProperties();
        Map<String, ThingModelProperty> sourceMap = source.thingModel2Map();

        ThingModelProperty sourceProperty;
        for (ThingModelProperty targetProperty : targetProperties) {
            sourceProperty = sourceMap.get(targetProperty.getIdentifier());
            if(sourceProperty == null){
                continue;
            }

            targetProperty.setName(sourceProperty.getName());
            targetProperty.setDataType(sourceProperty.getDataType());
        }
    }


    //**单火二路和单火三路的逻辑和以前的不一样
    public void thingSwitchModelPropertyExtend(ThingModel source) {
        List<ThingModelProperty> targetProperties = this.getProperties();
        Map<String, ThingModelProperty> sourceMap = source.thingModel2Map();

        ThingModelProperty sourceProperty;
        for (ThingModelProperty targetProperty : targetProperties) {
            sourceProperty = sourceMap.get(targetProperty.getIdentifier());
            if(sourceProperty == null){
                continue;
            }

            sourceProperty.setValue(targetProperty.getValue());
        }
        targetProperties.clear();
        for (Map.Entry<String,ThingModelProperty> entry: sourceMap.entrySet()){
            ThingModelProperty  targetProperty = new ThingModelProperty();
            targetProperty.setName(entry.getValue().getName());
            targetProperty.setValue(entry.getValue().getValue());
            targetProperty.setIdentifier(entry.getValue().getIdentifier());
            targetProperty.setDataType(entry.getValue().getDataType());
            targetProperties.add(targetProperty);
        }
    }


    public ThingModel simpleThingModel() {
        return ThingModel.builder()
                .properties(simpleProperties())
                .build();
    }

    public List<ThingModelProperty> simpleProperties() {
        List<ThingModelProperty> simpleProperties = new ArrayList<>();
        for (ThingModelProperty property : this.getProperties()) {
            if (property.getDataType().getType().equals("struct")) {
                simpleProperties.add(ThingModelProperty.builder()
                        .identifier(property.getIdentifier())
                        .value(property.getValue())
                        .build());
            } else {
                simpleProperties.add(ThingModelProperty.builder()
                        .identifier(property.getIdentifier())
                        .value(property.getValue() + "")
                        .build());
            }
        }
        return simpleProperties;
    }


    /**
     * 属性相同
     *
     * @return
     */
    public Boolean eqIdentifier(ThingModel target) {
        Set<String> sourceSet = this.identifierSet();
        Set<String> targetSet = target.identifierSet();

        for (String key : sourceSet) {
            if (!targetSet.contains(key)) {
                return false;
            }
            targetSet.remove(key);
        }
        for (String key : targetSet) {
            if (!sourceSet.contains(key)) {
                return false;
            }
        }
        return true;
    }
}
