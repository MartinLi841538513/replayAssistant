package com.alany.u2.utils;

import android.util.Log;

import com.alany.u2.base.BaseCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by alany on 2018/7/25.
 */
public class FileUtil {

    public static boolean deleteDir(File dir) {
        if(dir.isFile()){
            dir.delete();
            return ! dir.exists();
        }else{
            File[] files = dir.listFiles();
            if(files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                    files[i].delete();
                }
            }
            if(! dir.exists()) dir.mkdir();
            return dir.exists() && dir.list().length == 0;
        }
    }

    public static boolean removeDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean mkdir(String path, boolean isDeleted){
        File adTestFolder = new File(path);
        if(adTestFolder.exists()){
            if(isDeleted){
                removeDir(adTestFolder);
            }
            boolean isExpired = (System.currentTimeMillis() - adTestFolder.lastModified())/(1000*60) > 30;//大于30min
            if(! isDeleted && isExpired){//没有要求删除但是过期了还是要删除
                removeDir(adTestFolder);
            }

        }
        if(!adTestFolder.exists()){
            try {
                if(! adTestFolder.mkdir()){
                    Log.e(BaseCase.testTag,path + " 创建失败");
                    return false;
                }
                Log.i(BaseCase.testTag,path + " 创建成功");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return adTestFolder.exists();
    }

    public static boolean createFile(String path){
        File file = new File(path);
        if(!file.exists()){
            try {
                if(! file.createNewFile()){
                    Log.e(BaseCase.testTag,path + " 创建失败");
                    return false;
                }
                Log.i(BaseCase.testTag,path + " 创建成功");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file.exists();
    }

    public static ArrayList<String> getFileAllLine(File file) {
        try {
            FileReader fr = new FileReader(file.getPath());
            BufferedReader br = new BufferedReader(fr);
            ArrayList<String> list = new ArrayList<>();
            String ss = null;
            while ((ss = br.readLine()) != null) {
                list.add(ss);
            }
            fr.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
