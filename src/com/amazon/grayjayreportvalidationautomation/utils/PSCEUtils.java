package com.amazon.grayjayreportvalidationautomation.utils;

import com.amazon.ion.IonBool;
import com.amazon.ion.IonDatagram;
import com.amazon.ion.IonDecimal;
import com.amazon.ion.IonFloat;
import com.amazon.ion.IonInt;
import com.amazon.ion.IonList;
import com.amazon.ion.IonLob;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonText;
import com.amazon.ion.IonTimestamp;
import com.amazon.ion.IonValue;
import com.amazon.ion.util.IonValueUtils;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PSCEUtils<T> {
    AppConfigUtil appConfigUtil;
    CalendarUtil calendarUtil;

    @Inject
    public PSCEUtils(AppConfigUtil appConfigUtil, CalendarUtil calendarUtil){
        this.appConfigUtil = appConfigUtil;
        this.calendarUtil = calendarUtil;
    }

    public static final Logger LOGGER = LogManager.getLogger(PSCEUtils.class);

    public static Object ionToJava(IonValue ionValue) {
        if (IonValueUtils.anyNull(ionValue)) {
            return null;
        }
        else {
            switch (ionValue.getType()) {
                default:
                    throw new RuntimeException("Unsupported Ion Type: " + ionValue.getType().name());
                case BLOB:
                case CLOB:
                    return ((IonLob)ionValue).getBytes();
                case BOOL:
                    return ((IonBool)ionValue).booleanValue();
                case STRING:
                case SYMBOL:
                    return ((IonText)ionValue).stringValue();
                case DATAGRAM:
                    return ((IonDatagram)ionValue).getBytes();
                case DECIMAL:
                    return ((IonDecimal)ionValue).bigDecimalValue();
                case FLOAT:
                    return ((IonFloat)ionValue).doubleValue();
                case INT:
                    return ((IonInt)ionValue).bigIntegerValue();
                case LIST:
                    ArrayList<Object> javaArr = new ArrayList<>(((IonList)ionValue).size());
                    for (IonValue indexValue : ((IonList)ionValue)) {
                        javaArr.add(ionToJava(indexValue));
                    }
                    return javaArr;
                case STRUCT:
                    HashMap<String, Object> javaMap = new HashMap<>();
                    for (IonValue value : ((IonStruct)ionValue)) {
                        javaMap.put(value.getFieldName(), ionToJava(value));
                    }
                    return javaMap;
                case TIMESTAMP:
                    return ((IonTimestamp)ionValue).dateValue();
            }
        }
    }




}
