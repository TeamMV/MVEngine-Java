package dev.mv.engine.resources;

import dev.mv.engine.MVEngine;

import java.util.HashMap;
import java.util.Map;

public class R {
    private abstract static class Res<T>{
        protected Map<String, T> map = new HashMap<>();

        public T findByResourceId(String id) {
            try {
                return map.get(id);
            } catch (Exception e) {
                MVEngine.Exceptions.__throw__(new ResourceNotFoundException("There is no resource with resource-id of \"" + id + "\"!"));
            }
        }
    }

    public static class Textures{

    }
}
