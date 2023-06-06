package com.os467.lib;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DefaultClassScanner implements ClassScanner {

    //包路径
    private static final String DEFAULT_SCAN_PACKAGE = "com.os467";

    //字节码所在位置
    private static final String DEFAULT_CLASS_PATH = System.getProperty("user.dir") + "\\target\\classes";

    private String targetClassesPath = DEFAULT_CLASS_PATH;

    private String scanPackagePath = DEFAULT_SCAN_PACKAGE;

    //全类名列表
    private List<String> classNameList;


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
     * 注册包扫描路径
     * @param scanPackagePath example: com.os467
     */
    private void registerScanPath(String scanPackagePath){
        this.scanPackagePath = scanPackagePath;
    }

    /**
     * 注册包扫描路径和字节码文件路径
     * @param scanPackagePath example: com.os467
     * @param targetClassesPath example: System.getProperty("user.dir") + "\\target\\classes";
     */
    private void registerScanPath(String scanPackagePath,String targetClassesPath){
        this.scanPackagePath = scanPackagePath;
        this.targetClassesPath = targetClassesPath;
    }


    /**
     * 扫描类类型
     */
    private void scanClassType() {
        File file = new File(targetClassesPath + "\\" + scanPackagePath.replace(".","\\"));
        handleFile(file, scanPackagePath);
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
                if (className.charAt(0) == '.'){
                    className = className.substring(1);
                }
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
