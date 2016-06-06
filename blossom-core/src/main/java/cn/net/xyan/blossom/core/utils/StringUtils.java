package cn.net.xyan.blossom.core.utils;

import java.util.List;

/**
 * Created by zarra on 16/4/23.
 */
public class StringUtils {
    static public boolean isEmpty(String string){
        return string == null || string.length() == 0;
    }

    static public String join(List<String> stringList,String sep){
        if (stringList.size()>0){

            if (stringList.size() == 1){
                return stringList.get(0);
            }else {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i=0;i<stringList.size();i++){
                    if (i >0){
                        stringBuffer.append(sep);
                    }
                    stringBuffer.append(stringList.get(i));
                }

                return stringBuffer.toString();
            }

        }else{
            return null;
        }
    }
}
