import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;

public class FoldersTask {
    
    // function to loop over the directory tree and append data of images.   
    public void directoryList(File parentDir, String outputPath, StringBuilder sb) {
        File elements[] = parentDir.listFiles();
        Arrays.sort(elements);
        SimpleDateFormat sd = new SimpleDateFormat("E MMM d HH:mm:ss YYYY");
       
        for(File element: elements){
            if(element.isFile()){
                try{
                    Path path = Paths.get(element.getAbsolutePath());
                    sb.append(removePrefix(element.getName())).append(",");
                    sb.append(String.format("%d kB", Files.size(path) / 1024)).append(",");
                    sb.append(sd.format(element.lastModified())).append(",");
                    sb.append("\n");
                    File copyImage = new File(outputPath + "/" + element.getName());
                    Files.copy(element.toPath(), copyImage.toPath());
                }catch(Exception e){}
            } else if(element.isDirectory()){
                directoryList(element.getAbsoluteFile(), outputPath, sb);
            }
        }
        
    }
      
    // function to discard the prefix of the file name
    public static String removePrefix(String str){
        String result = "";
        int n = str.length();
        int index = 0;
        
        for(int i = 0; i < n; i++){
            if(str.charAt(i) == ' ' || str.charAt(i) == '_' ||  str.charAt(i) == '-'){
                index = i;
                break;
            }
        }
        try{
            result = str.substring(index+1,n);        
        } catch(Exception e) {}
        return result;
    }
    
    public void solve() {
        // getting file path from the user
        Scanner input = new Scanner(System.in);
        System.out.print("Enter folder path: ");
        String inputFilePath = input.nextLine();
        File parentDirectory = new File(inputFilePath);
        
        // creating the output directory
        String absolutePath = new File("").getAbsolutePath();
        File outputDir = new File(absolutePath + "/output");
        if(outputDir.exists()){
            int counter = 1;
            while(outputDir.exists()){
                String newFilename = MessageFormat.format("{0}_{1}", "output", counter++);
                outputDir = new File(absolutePath+ "/" +newFilename);
                if(outputDir.mkdir()){
                    break;
                }
            }
        } else {
            outputDir.mkdir();
        }
        
        // creating the images directory
        File imagesDir = new File(outputDir.toPath() + "/images_dataset");
        imagesDir.mkdir();
        
        //creating the csv file
        StringBuilder sb = new StringBuilder();
        sb.append("Image").append(",").append("Image Size").append(",").append("Image Modification Date").append("\n");
        directoryList(parentDirectory, imagesDir.toString(), sb);
        try(FileWriter writer = new FileWriter(outputDir.toPath() + "/report.csv")){
          writer.write(sb.toString());
          System.out.println("csv has been created! Now, you can see the report.");
        }catch (Exception e) {}
    }
    
    public static void main(String[] args) {
       (new FoldersTask()).solve();
    }
}
