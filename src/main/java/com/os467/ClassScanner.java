package com.os467;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    //包路径
    private static final String PACKAGE = "com.os467";

    //字节码所在位置
    private static final String SCAN_PATH = System.getProperty("user.dir") + "\\target\\classes\\" + PACKAGE.replace(".","\\");

    //全类名列表
    private static List<String> classNameList;

    /**
     * 获取字节码列表
     */
    public List<String> getClassNameList(){
        if (classNameList == null){
            synchronized (this){
                if (classNameList == null){
                    classNameList = new ArrayList<>();
                    scanClassType();
                }
            }
        }
        return classNameList;
    }

    /**
     * 扫描类类型
     */
    private void scanClassType() {
        File file = new File(SCAN_PATH);
        handleFile(file,PACKAGE);
    }

    private void handleFile(File file,String packagePath) {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                handleFile(files[i],packagePath+"."+files[i].getName());
            }
        }else {
            //不是一个文件夹
            String extendName = getExtendName(file.getName());
            if (extendName.equals(".class")){
                String className = packagePath.replace(".class", "");
                classNameList.add(className);
            }
        }
    }

    /**
     * 获取文件拓展名
     * @param fileName
     * @return
     */
    private String getExtendName(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

}
