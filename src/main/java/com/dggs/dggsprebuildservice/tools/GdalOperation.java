package com.dggs.dggsprebuildservice.tools;

import java.io.*;

/**
 * 构建gdal进程操作类
 */
public class GdalOperation {

    /**
     * 构建命令行进程
     *
     * @param command 命令参数
     * @param report  状态报告
     */
    public static void gdalDoOperation(String[] command, OperationReport report) {
        try {
            System.out.println(report.getNotice());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            processBuilder.redirectErrorStream(true); // 将错误输出流与标准输出流合并
            processBuilder.directory(new File(".")); // 设置工作目录（如果需要的话）
            // 获取进程的标准输出流
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // 读取并打印进程的实时日志
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建图像重采样命令
     *
     * @param input  输入图像
     * @param output 输出图像
     * @param method 采样方法
     * @param width  宽度
     * @param height 高度
     * @return
     */
    public static String[] getResampleCommand(String input, String output, String method, int width, int height) {
        return new String[]{
                "gdalwarp",
                "-r",
                method,
                "ts",
                String.valueOf(width),
                String.valueOf(height),
                input,
                output
        };
    }

}
