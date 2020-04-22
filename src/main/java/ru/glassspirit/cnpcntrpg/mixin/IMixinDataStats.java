package ru.glassspirit.cnpcntrpg.mixin;

import java.util.Map;

public interface IMixinDataStats {

    int getLevel();

    void setLevel(int level);

    Map<String, Object> getCustomData();

    Map<String, Double> getProperties();

}
