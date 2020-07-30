package com.liangzhmj.cat.tools.json;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统中所有要转json格式输入的父类
 *
 * @author liangzhmj
 */
@Log4j2
public class JSONBase {

    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String DOUBLE = "Double";
    public static final String STRING = "String";
    public static final String JSONOBJECT = "JSONOBJECT";
    public static final String BOOLEAN = "Boolean";

    /**
     * 转化为JSONObject对象
     *
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        Field[] fs = this.getClass().getDeclaredFields();
        if (fs != null && fs.length > 0) {
            for (Field field : fs) {
                try {
                    JSONField cf = field.getAnnotation(JSONField.class);
                    if (cf == null) {
                        continue;
                    }
                    String name = cf.name();
                    name = !StringUtils.isEmpty(name) ? name : field.getName();
                    Object value = PropertyUtils.getProperty(this, field.getName());
                    if (value == null) {
                        continue;
                    }
                    json = JsonUtils.setValue(json, name, value);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        } else {
            log.error(this.getClass() + ":没有属性");
        }
        return json;
    }

    /**
     * 把json中对应的属性注入到实现类中
     *
     * @param json
     */
    public JSONBase fromJSON(JSONObject json) {
        if (json == null || json.isEmpty()) {
            log.error(this.getClass() + ":传入的JSON为null");
            return this;
        }
        Field[] fs = this.getClass().getDeclaredFields();
        if (fs != null && fs.length > 0) {
            for (Field field : fs) {
                try {
                    JSONField cf = field.getAnnotation(JSONField.class);
                    if (cf == null) {
                        continue;
                    }
                    String name = cf.name();
                    String clazz = cf.clazz();
                    name = !StringUtils.isEmpty(name) ? name : field.getName();
                    if (json.containsKey(name)) {
                        Object value = null;
                        //处理类型
                        if (!StringUtils.isEmpty(clazz)) {
                            if (INTEGER.equals(clazz)) {
                                try {
                                    value = json.getInt(name);
                                } catch (Exception e) {
                                    value = 0;
                                }
                            } else if (LONG.equals(clazz)) {
                                try {
                                    value = json.getLong(name);
                                } catch (Exception e) {
                                    value = 0;
                                }
                            } else if (DOUBLE.equals(clazz)) {
                                try {
                                    value = json.getDouble(name);
                                } catch (Exception e) {
                                    value = 0;
                                }
                            } else {
                                value = json.get(name);
                            }
                        } else {
                            value = json.get(name);
                        }
                        if (value == null || value instanceof JSONNull) {
                            continue;
                        }
                        if (value instanceof JSONObject) {
                            if (JSONOBJECT.equals(clazz)) {//不转换为jsonbase,直接赋值
                                PropertyUtils.setProperty(this, field.getName(), value);
                            } else {
                                //如果是个json对象
                                JSONBase temp = (JSONBase) Class.forName(field.getType().getName()).newInstance();
                                temp.fromJSON(JSONObject.fromObject(value));
                                PropertyUtils.setProperty(this, field.getName(), temp);
                            }
                            continue;
                        }
                        if (value instanceof JSONArray) {
                            //暂时只支持List
                            List<Object> vs = new ArrayList<Object>();
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            Type t = pt.getActualTypeArguments()[0];
                            String classStr = t.toString().substring(6);
                            JSONArray ja = JSONArray.fromObject(value);
                            int size = ja.size();
                            for (int i = 0; i < size; i++) {
                                if (classStr.endsWith("." + INTEGER)) {
                                    vs.add(ja.getInt(i));
                                } else if (classStr.endsWith("." + LONG)) {
                                    vs.add(ja.getLong(i));
                                } else if (classStr.endsWith("." + DOUBLE)) {
                                    vs.add(ja.getDouble(i));
                                } else if (classStr.endsWith("." + STRING)) {
                                    vs.add(ja.getString(i));
                                } else {
                                    JSONBase temp = (JSONBase) Class.forName(classStr).newInstance();
                                    temp.fromJSON(ja.getJSONObject(i));
                                    vs.add(temp);
                                }
                            }
                            PropertyUtils.setProperty(this, field.getName(), vs);
                            continue;
                        }
                        PropertyUtils.setProperty(this, field.getName(), value);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        } else {
            log.error(this.getClass() + ":没有属性");
        }
        return this;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
