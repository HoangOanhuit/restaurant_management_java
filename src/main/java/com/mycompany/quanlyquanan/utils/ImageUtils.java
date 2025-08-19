
package com.mycompany.quanlyquanan.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utility class để xử lý hình ảnh - ĐÃ CẬP NHẬT CHO DISH IMAGES
 * @author Admin
 */
public class ImageUtils {
    
    // Đường dẫn mặc định cho hình ảnh
    private static final String DEFAULT_DISH_IMAGE = "/assets/images/dish/default.jpg";
    private static final String DISH_IMAGES_PATH = "/assets/images/dish/";
    private static final String EMPLOYEE_IMAGES_PATH = "/assets/images/employee/";
    
    public static String saveImage(File sourceFile) throws IOException {
        String destDir = "assets/images/";
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
    
    /**
     * Load hình ảnh món ăn từ resources
     * @param imagePath đường dẫn tương đối (vd: "/assets/images/dish/pho_bo_tai.jpg")
     * @return ImageIcon hoặc null nếu không tìm thấy
     */
    public static ImageIcon loadDishImage(String imagePath) {
        return loadImageFromResources(imagePath);
    }
    
    /**
     * Load hình ảnh món ăn với kích thước cụ thể
     * @param imagePath đường dẫn hình ảnh
     * @param width chiều rộng mong muốn
     * @param height chiều cao mong muốn
     * @return ImageIcon đã resize
     */
    public static ImageIcon loadDishImage(String imagePath, int width, int height) {
        ImageIcon icon = loadDishImage(imagePath);
        if (icon != null) {
            return resizeImageIcon(icon, width, height);
        }
        
        // Nếu không load được, dùng hình mặc định
        ImageIcon defaultIcon = loadImageFromResources(DEFAULT_DISH_IMAGE);
        if (defaultIcon != null) {
            return resizeImageIcon(defaultIcon, width, height);
        }
        
        // Tạo placeholder nếu không có hình nào
        return createPlaceholderImage(width, height, "Không có hình");
    }
    
    /**
     * Load hình ảnh từ tên file (tự động tìm trong thư mục dish)
     * @param filename tên file (vd: "pho_bo_tai.jpg")
     * @return ImageIcon
     */
    public static ImageIcon loadDishImageByFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return loadImageFromResources(DEFAULT_DISH_IMAGE);
        }
        
        String fullPath = DISH_IMAGES_PATH + filename;
        return loadDishImage(fullPath);
    }
    
    /**
     * Load hình ảnh từ resources với fallback
     * @param imagePath đường dẫn hình ảnh
     * @return ImageIcon hoặc placeholder
     */
    public static ImageIcon loadImageFromResources(String imagePath) {
        try {
            // Thử load từ resources
            URL imageURL = ImageUtils.class.getResource(imagePath);
            if (imageURL != null) {
                return new ImageIcon(imageURL);
            }
            
            // Thử load từ classpath
            InputStream inputStream = ImageUtils.class.getResourceAsStream(imagePath);
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                return new ImageIcon(image);
            }
            
            // Thử load từ file system (cho development)
            String projectPath = System.getProperty("user.dir");
            String fullPath = projectPath + "/src/main/resources" + imagePath;
            File imageFile = new File(fullPath);
            
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                return new ImageIcon(image);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Resize ImageIcon về kích thước mới
     * @param icon ImageIcon gốc
     * @param width chiều rộng mới
     * @param height chiều cao mới
     * @return ImageIcon đã resize
     */
    public static ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        
        Image image = icon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    /**
     * Tạo hình placeholder khi không load được hình thật
     * @param width chiều rộng
     * @param height chiều cao
     * @param text text hiển thị
     * @return ImageIcon placeholder
     */
    public static ImageIcon createPlaceholderImage(int width, int height, String text) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        
        // Thiết lập antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Nền gradient
        GradientPaint gradient = new GradientPaint(0, 0, Color.LIGHT_GRAY, width, height, Color.GRAY);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Viền
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(1, 1, width-2, height-2);
        
        // Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.min(width/8, height/8)));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fm.getAscent();
        
        // Shadow effect
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x+1, y+1);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
    
    /**
     * Tạo hình tròn từ ImageIcon
     * @param icon ImageIcon gốc
     * @param diameter đường kính
     * @return ImageIcon hình tròn
     */
    public static ImageIcon createCircularImage(ImageIcon icon, int diameter) {
        if (icon == null) return null;
        
        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = circularImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Tạo clip hình tròn
        g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, diameter, diameter));
        
        // Vẽ hình gốc
        Image scaledImage = icon.getImage().getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);
        g2d.drawImage(scaledImage, 0, 0, null);
        
        g2d.dispose();
        return new ImageIcon(circularImage);
    }
    
    /**
     * Load hình ảnh nhân viên
     * @param imagePath đường dẫn hình ảnh
     * @return ImageIcon
     */
    public static ImageIcon loadEmployeeImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return loadImageFromResources("/assets/images/employee/default.jpg");
        }
        return loadImageFromResources(imagePath);
    }
    
    /**
     * Load và resize hình ảnh cho JTable cell
     * @param imagePath đường dẫn hình ảnh
     * @param cellWidth chiều rộng cell
     * @param cellHeight chiều cao cell
     * @return ImageIcon phù hợp với cell
     */
    public static ImageIcon loadImageForTableCell(String imagePath, int cellWidth, int cellHeight) {
        // Để margin 4px mỗi bên
        int imageWidth = cellWidth - 8;
        int imageHeight = cellHeight - 8;
        
        ImageIcon icon = loadDishImage(imagePath);
        if (icon != null) {
            return resizeImageIcon(icon, imageWidth, imageHeight);
        }
        
        return createPlaceholderImage(imageWidth, imageHeight, "No Image");
    }
    
    /**
     * Kiểm tra file hình ảnh có tồn tại không
     * @param imagePath đường dẫn hình ảnh
     * @return true nếu tồn tại
     */
    public static boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Kiểm tra trong resources
            URL imageURL = ImageUtils.class.getResource(imagePath);
            if (imageURL != null) {
                return true;
            }
            
            // Kiểm tra trong file system
            String projectPath = System.getProperty("user.dir");
            String fullPath = projectPath + "/src/main/resources" + imagePath;
            File imageFile = new File(fullPath);
            
            return imageFile.exists() && imageFile.isFile();
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lấy danh sách tất cả hình ảnh dish có sẵn
     * @return mảng tên file
     */
    public static String[] getAvailableDishImages() {
        String projectPath = System.getProperty("user.dir");
        String dishImagesPath = projectPath + "/src/main/resources/assets/images/dish";
        
        File dishDir = new File(dishImagesPath);
        if (dishDir.exists() && dishDir.isDirectory()) {
            File[] imageFiles = dishDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".png")
            );
            
            if (imageFiles != null) {
                String[] fileNames = new String[imageFiles.length];
                for (int i = 0; i < imageFiles.length; i++) {
                    fileNames[i] = imageFiles[i].getName();
                }
                return fileNames;
            }
        }
        
        return new String[0];
    }
    
    /**
     * Tạo preview hình ảnh cho món ăn
     * @param imagePath đường dẫn hình ảnh
     * @param previewSize kích thước preview (vuông)
     * @return ImageIcon preview
     */
    public static ImageIcon createDishPreview(String imagePath, int previewSize) {
        ImageIcon originalIcon = loadDishImage(imagePath);
        if (originalIcon == null) {
            return createPlaceholderImage(previewSize, previewSize, "No Preview");
        }
        
        // Tạo preview với border và shadow effect
        BufferedImage preview = new BufferedImage(previewSize, previewSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = preview.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Tạo shadow
        int shadowOffset = 3;
        int imageSize = previewSize - shadowOffset;
        
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(shadowOffset, shadowOffset, imageSize, imageSize, 10, 10);
        
        // Vẽ hình chính
        Image scaledImage = originalIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        g2d.drawImage(scaledImage, 0, 0, imageSize, imageSize, null);
        
        // Vẽ border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, imageSize-1, imageSize-1, 10, 10);
        
        g2d.dispose();
        return new ImageIcon(preview);
    }
    
    /**
     * Tạo thumbnail cho JList hoặc JComboBox
     * @param imagePath đường dẫn hình ảnh
     * @param size kích thước thumbnail
     * @return ImageIcon thumbnail
     */
    public static ImageIcon createThumbnail(String imagePath, int size) {
        ImageIcon icon = loadDishImage(imagePath);
        if (icon == null) {
            return createPlaceholderImage(size, size, "?");
        }
        
        return resizeImageIcon(icon, size, size);
    }
    
    /**
     * Tạo ImageIcon cho button với text overlay
     * @param imagePath đường dẫn hình ảnh
     * @param text text hiển thị trên hình
     * @param width chiều rộng
     * @param height chiều cao
     * @return ImageIcon với text overlay
     */
    public static ImageIcon createButtonImageWithText(String imagePath, String text, int width, int height) {
        BufferedImage buttonImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buttonImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Vẽ hình nền
        ImageIcon bgIcon = loadDishImage(imagePath);
        if (bgIcon != null) {
            Image bgImage = bgIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            g2d.drawImage(bgImage, 0, 0, null);
            
            // Tạo overlay mờ
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, width, height);
        } else {
            // Nền gradient nếu không có hình
            GradientPaint gradient = new GradientPaint(0, 0, Color.DARK_GRAY, width, height, Color.BLACK);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
        
        // Vẽ text
        if (text != null && !text.trim().isEmpty()) {
            g2d.setColor(Color.WHITE);
            Font font = new Font("Arial", Font.BOLD, Math.min(width/10, height/8));
            g2d.setFont(font);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            int x = (width - textWidth) / 2;
            int y = (height - textHeight) / 2 + fm.getAscent();
            
            // Text shadow
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, x+1, y+1);
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);
        }
        
        g2d.dispose();
        return new ImageIcon(buttonImage);
    }
    
    /**
     * Lấy đường dẫn hình ảnh từ tên món ăn
     * @param dishName tên món ăn
     * @return đường dẫn hình ảnh tương ứng
     */
    public static String getDishImagePath(String dishName) {
        if (dishName == null || dishName.trim().isEmpty()) {
            return DEFAULT_DISH_IMAGE;
        }
        
        // Mapping tên món ăn với file hình ảnh
        String filename = switch (dishName.toLowerCase().trim()) {
            case "phở bò tái" -> "pho_bo_tai.jpg";
            case "cơm gà nướng" -> "com_ga_nuong.jpg";
            case "bún bò huế" -> "bun_bo_hue.jpg";
            case "lẩu kim chi" -> "lau_kim_chi.jpg";
            case "lẩu thái" -> "lau_thai.jpg";
            case "cafe sữa đá" -> "cafe_sua_da.jpg";
            case "trà đá" -> "tra_da.jpg";
            case "nước chanh" -> "nuoc_chanh.jpg";
            case "nem rán" -> "nem_ran.jpg";
            case "salad rau củ" -> "salad_rau_cu.jpg";
            case "bánh flan" -> "banh_flan.jpg";
            case "chè đậu đỏ" -> "che_dau_do.jpg";
            default -> "default.jpg";
        };
        
        return DISH_IMAGES_PATH + filename;
    }
    
    /**
     * Validate hình ảnh upload
     * @param file file hình ảnh
     * @return true nếu hợp lệ
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        
        for (String ext : validExtensions) {
            if (fileName.endsWith(ext)) {
                // Kiểm tra kích thước file (max 5MB)
                long maxSize = 5 * 1024 * 1024; // 5MB
                if (file.length() <= maxSize) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Compress hình ảnh để tiết kiệm dung lượng
     * @param originalIcon ImageIcon gốc
     * @param quality chất lượng (0.1 - 1.0)
     * @return ImageIcon đã compress
     */
    public static ImageIcon compressImage(ImageIcon originalIcon, float quality) {
        if (originalIcon == null) return null;
        
        try {
            BufferedImage bufferedImage = new BufferedImage(
                originalIcon.getIconWidth(),
                originalIcon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB
            );
            
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalIcon.getImage(), 0, 0, null);
            g2d.dispose();
            
            return new ImageIcon(bufferedImage);
            
        } catch (Exception e) {
            System.err.println("Error compressing image: " + e.getMessage());
            return originalIcon;
        }
    }
    
    /**
     * Tạo collage từ nhiều hình ảnh món ăn
     * @param imagePaths danh sách đường dẫn hình ảnh
     * @param collageWidth chiều rộng collage
     * @param collageHeight chiều cao collage
     * @return ImageIcon collage
     */
    public static ImageIcon createDishCollage(String[] imagePaths, int collageWidth, int collageHeight) {
        if (imagePaths == null || imagePaths.length == 0) {
            return createPlaceholderImage(collageWidth, collageHeight, "No Images");
        }
        
        BufferedImage collage = new BufferedImage(collageWidth, collageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = collage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Tính toán grid
        int cols = (int) Math.ceil(Math.sqrt(imagePaths.length));
        int rows = (int) Math.ceil((double) imagePaths.length / cols);
        
        int cellWidth = collageWidth / cols;
        int cellHeight = collageHeight / rows;
        
        // Vẽ từng hình
        for (int i = 0; i < imagePaths.length; i++) {
            int row = i / cols;
            int col = i % cols;
            
            int x = col * cellWidth;
            int y = row * cellHeight;
            
            ImageIcon icon = loadDishImage(imagePaths[i]);
            if (icon != null) {
                Image scaledImage = icon.getImage().getScaledInstance(cellWidth-2, cellHeight-2, Image.SCALE_SMOOTH);
                g2d.drawImage(scaledImage, x+1, y+1, null);
            }
            
            // Vẽ border
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, cellWidth-1, cellHeight-1);
        }
        
        g2d.dispose();
        return new ImageIcon(collage);
    }
}