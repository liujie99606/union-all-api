package com.union.base.util;

import cn.hutool.core.util.ObjectUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StrUtil extends cn.hutool.core.util.StrUtil {

    public static String join(Object[] array, String separator) {
        return array == null ? null : join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }

            int noOfItems = endIndex - startIndex;
            if (noOfItems <= 0) {
                return "";
            } else {
                StringBuilder buf = new StringBuilder(noOfItems * 16);

                for (int i = startIndex; i < endIndex; ++i) {
                    if (i > startIndex) {
                        buf.append(separator);
                    }

                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }

                return buf.toString();
            }
        }
    }

    public static String join(Iterable<?> iterable, String separator) {
        return iterable == null ? null : join(iterable.iterator(), separator);
    }

    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                String result = ObjectUtil.toString(first);
                return result;
            } else {
                StringBuilder buf = new StringBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    public static int getScale(String data) {
        BigDecimal bd = new BigDecimal(data);
        return bd.scale();
    }

    public static String addComma(String data) {
        String[] arr = data.split(",");
        String ndata = "";
        for (String str : arr) {
            ndata += "'" + str + "',";
        }
        return ndata.substring(0, ndata.length() - 1);
    }

    public static int genNewId(int id) {
        return Integer.parseInt("1" + String.format("%04d", id));
    }

    public static List<String> loadSql(String sqlFile) throws Exception {
        List<String> sqlList = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(sqlFile);
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            StringBuffer sb = new StringBuffer();
            while ((s = br.readLine()) != null) {
                if (s.startsWith("/*") || s.startsWith("--")) {

                } else if (s.endsWith(";")) {
                    sb.append(s);
                    sqlList.add(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    sb.append(s);
                }

            }
            fr.close();
            br.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return sqlList;
    }

    public static String removeWhere(String scripts) {
        return scripts.substring(0, scripts.indexOf("where"));
    }

    public static void main(String[] args) {
        //System.out.println(getScale("0.3795"));
        //System.out.println(DateUtil.parse("2014-06-12 00:00:00").toString("yyyy"));
//        DateTime mdate = DateUtil.parseDate("2017-1-1");
//        DateTime sdate = DateUtil.parseDate("2016/06/29");
//        System.out.println(mdate.toString("yyyy-MM-dd"));
//        System.out.println(sdate.toString("yyyy-MM-dd"));
        String str = "union.ryTask.ryParams";
        System.out.println(str.substring(0, str.lastIndexOf(".")));
        System.out.println(str.substring(str.lastIndexOf(".") + 1));
    }
}
