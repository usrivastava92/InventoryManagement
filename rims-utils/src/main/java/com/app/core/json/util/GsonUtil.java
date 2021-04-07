 package com.app.core.json.util;
 
 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import java.io.Serializable;
 import java.lang.reflect.Type;
 
 
 
 
 
 
 public class GsonUtil
 {
   private static final Gson gson = (new GsonBuilder()).create();
   
   public static String convertToJson(Object obj) {
     if (obj != null) {
       return gson.toJson(obj);
     }
     return null;
   }
   
   public static String convertToJson(Object obj, Type type) {
     if (obj != null && type != null) {
       return gson.toJson(obj, type);
     }
     return null;
   }
   
   public static <T extends Serializable> T parseJson(String json, Type type) {
     if (json != null && type != null) {
       return (T)(Serializable)gson.fromJson(json, type);
     }
     return null;
   }
 }



