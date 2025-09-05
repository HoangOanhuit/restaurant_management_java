/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.awt.Image;
/**
 *
 * @author Admin
 */
public class ImageUtils {
     public static String saveImage(File sourceFile,String destinationFolder) throws IOException {
          String destDir = destinationFolder != null ? destinationFolder : "assets/images/";
        File destFolder = new File(destDir);
        if (!destFolder.exists()) {
            destFolder.mkdirs(); // tạo thư mục nếu chưa có
        }

        // Tạo tên file duy nhất
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        Path destination = Paths.get(destDir + fileName);
        Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString(); // trả về đường dẫn ảnh đã lưu
    }

    public static File chooseImageFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static ImageIcon loadImage(String absolutePath, int width, int height) {
        if (absolutePath == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(absolutePath);
        if (width > 0 && height > 0) {
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return icon;
    }
}
//File selectedImage = ImageUtils.chooseImageFile(this);
//if (selectedImage != null) {
//    try {
//        String savedPath = ImageUtils.saveImage(selectedImage);
//        System.out.println("Ảnh đã lưu: " + savedPath);
//        employee.setAvatar(savedPath); // gán vào model để lưu DB
//    } catch (IOException ex) {
//        ex.printStackTrace();
//        JOptionPane.showMessageDialog(this, "Lỗi lưu ảnh");
//    }
//}