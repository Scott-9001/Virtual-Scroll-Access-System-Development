import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.*;
import java.time.LocalDateTime;

public class ScrollManager {
    public Map<String, Scroll> scrollsByScrollId = new HashMap<String, Scroll>();
    private static final String FILE_PATH = "scrolls.txt";
    private static final String LOG_PATH = "log.txt";
    private static final String SCROLLS_FOLDER = "scrolls/empty.txt"; //where we want the scrolls to go
    private int successfulLoad;

    public ScrollManager() {
        this.successfulLoad = loadScrollsFromFile();
    }

    // View scrolls with options to filter, preview, or download
    public void viewScrolls(Scanner scanner, User currentUser) {
        while (true) {
            List<Scroll> filteredScrolls = new ArrayList<>(scrollsByScrollId.values());

            // Offer filtering options
            System.out.print("Do you want to apply any filters, or cancel to return to menu? (yes/no/cancel): ");
            String applyFilters = scanner.nextLine().trim();
            if (applyFilters.equalsIgnoreCase("yes")) {
                filteredScrolls = applyFilters(scanner, filteredScrolls);
            }
            else if (applyFilters.equalsIgnoreCase("cancel")) {
                return;
            }

            System.out.println("\nAvailable Scrolls:");
            // Display filtered scrolls
            for (int i = 0; i < filteredScrolls.size(); i++) {
                Scroll scroll = filteredScrolls.get(i);
                System.out.println((i + 1) + ". " + scroll.viewInfo());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    continue;
                }    
            }

            boolean dontStartSearchAgain = true;
            while (dontStartSearchAgain) {
                // Provide options to preview or download
                System.out.println("\nOptions:");
                System.out.println("  1. Preview a scroll");
                System.out.println("  2. Download a scroll");
                System.out.println("  3. Restart search");
                System.out.println("  4. Exit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        previewScroll(scanner, filteredScrolls);
                        break;
                    case "2":
                        if (currentUser.getUserType().equals("Guest")) {
                            System.out.println("Sorry, you need to sign up / log in to download scrolls.");
                        }
                        else {
                            downloadScroll(scanner, filteredScrolls, currentUser);
                        }
                        break;
                    case "3":
                        dontStartSearchAgain = false;
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    // Apply filters to scroll list
    List<Scroll> applyFilters(Scanner scanner, List<Scroll> scrolls) {
        List<Scroll> filteredScrolls = new ArrayList<>(scrolls);

        System.out.println("\nFilter options:");
        System.out.println("  1. Filter by uploader ID");
        System.out.println("  2. Filter by scroll ID");
        System.out.println("  3. Filter by name");
        System.out.println("  4. Filter by upload date");
        System.out.println("  5. No filter");
        System.out.print("Choose a filter option: ");
        String filterOption = scanner.nextLine().trim();
        Pattern matcher;
        switch (filterOption) {
            case "1":
                System.out.print("Enter uploader ID: ");
                String uploaderId = scanner.nextLine().trim();
                matcher = Pattern.compile(".*" + uploaderId.toLowerCase() + ".*");
                filteredScrolls.removeIf(scroll -> !matcher.matcher(scroll.getUploaderId().toLowerCase()).matches());
                break;
            case "2":
                System.out.print("Enter scroll ID: ");
                String scrollId = scanner.nextLine().trim();
                matcher = Pattern.compile(".*" + scrollId.toLowerCase() + ".*");
                filteredScrolls.removeIf(scroll -> !matcher.matcher(scroll.getScrollId().toLowerCase()).matches());
                break;
            case "3":
                System.out.print("Enter scroll name: ");
                String name = scanner.nextLine().trim();
                matcher = Pattern.compile(".*" + name.toLowerCase() + ".*");
                filteredScrolls.removeIf(scroll -> !matcher.matcher(scroll.getName().toLowerCase()).matches());
                break;
            case "4":
                System.out.print("Enter upload date (YYYY-MM-DD): ");
                String date = scanner.nextLine().trim();
                filteredScrolls.removeIf(scroll -> !scroll.getUploadDate().toString().equals(date));
                break;
            case "5":
                 // No filter
                break;
            default:
                System.out.println("Invalid filter option. No filters applied.");
        }
        return filteredScrolls;
    }

    // Preview scroll
    void previewScroll(Scanner scanner, List<Scroll> scrolls) {
        System.out.print("Enter the number of the scroll to preview: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < scrolls.size()) {
                Scroll scroll = scrolls.get(index);
                if (!scroll.getOriginalType().equals(".txt")) {
                    System.out.println("Sorry, preview is unvaliable for this type of file. ");
                    return;
                }
                String filePath = getScrollResourcePath(scroll.getName());
                File file = new File(filePath);
                if (file.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        System.out.println("\n--- Scroll Preview ---");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        System.out.println("--- End of Preview ---\n");
                    } catch (IOException e) {
                        System.out.println("Error reading the scroll file: " + e.getMessage());
                    }
                } else {
                    System.out.println("Scroll file not found.");
                }
            } else {
                System.out.println("Invalid scroll number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Scroll number is the number on the left. Please enter a valid number next time.");
        } catch (Exception e) {
            System.out.println("Error in preview. Please inform an administrator.");
        }
    }

    // Download scroll
    void downloadScroll(Scanner scanner, List<Scroll> scrolls, User currentUser) {
        System.out.print("Enter the number of the scroll to download: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < scrolls.size()) {
                Scroll scroll = scrolls.get(index);
                String filePath = getScrollResourcePath(scroll.getName());
                File file = new File(filePath);
                if (file.exists()) {
                    String destinationPath;
                    while (true) {
                        System.out.print("Enter the destination path to save the scroll to: ");
                        destinationPath = scanner.nextLine().trim();
                        File checkDir = new File(destinationPath);
                        if (!checkDir.isDirectory()) {
                            System.out.println("Invalid directory. Try again? (yes|no) ");
                            String reply = scanner.nextLine().trim();
                            if (reply.equalsIgnoreCase("yes")) {
                                continue;
                            } else {
                                return;
                            }
                        } else {
                            break;
                        }
                    }
                    File fullDestinationPath = new File(destinationPath + "/" + scroll.getName() + scroll.getOriginalType());
                    if (!fullDestinationPath.createNewFile()) {    
                        System.out.println("Filename already exists. Overwrite content? (yes|no) ");
                        String reply = scanner.nextLine().trim();
                        if (!reply.equalsIgnoreCase("yes")) {
                            System.out.println("Stopping.");
                            return;
                        }
                    }
                    try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(fullDestinationPath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        scroll.downloadScroll();
                        saveScrollMetadataToFile();
                        System.out.println("Scroll downloaded successfully to " + destinationPath);
                        appendLogFile(currentUser, "downloaded Scroll " + scroll.getScrollId());
                    } catch (IOException e) {
                        System.out.println("Error downloading the scroll: " + e.getMessage());
                    }
                } else {
                    System.out.println("Scroll file not found.");
                }
            } else {
                System.out.println("Invalid scroll number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Scroll number is the number on the left. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error in download. Please inform an administrator.");
        }
    }

    // Helper method to get the path to the scrolls folder
    private String getScrollResourcePath(String fileName) {
        try {
            ClassLoader classLoader = ScrollManager.class.getClassLoader();
            URL resource = classLoader.getResource(SCROLLS_FOLDER);
            if (resource == null) {
                throw new RuntimeException("Scrolls folder resource not found.");
            }
            String pathString = resource.toURI().getPath();
            return pathString.substring(0, pathString.lastIndexOf("/")) + "/" + fileName + ".bin";
        } catch (Exception e) {
            System.out.println("Error getting resource path: " + e.getMessage());
            return null;
        }
    }
 
    // Helper method to save the metadata of scrolls to a file (scrolls.txt)
    private void saveScrollMetadataToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getScrollFile(), false))) {
            for (Scroll scroll : scrollsByScrollId.values()) {
                writer.write(scroll.toString());
                writer.newLine();
            }
        }
    }

    private void saveNewScrollMetadataToFile(Scroll scroll) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getScrollFile(), true))) {
            writer.write(scroll.toString());
            writer.newLine();
        }
    }

    private int appendLogFile(User user, String action) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getLogFile(), true))) {
            writer.write(LocalDateTime.now().toString() + ": User " + user.getIdKey() + " " + action);
            writer.newLine();
            return 0;
        } catch (IOException e) {
            return 1;
        }
    }

    //Methods
    public int getSuccess() {
        return successfulLoad;
    }

    private String getScrollFile() {
        try {
            ClassLoader classLoader = ScrollManager.class.getClassLoader();
            return classLoader.getResource(FILE_PATH).toURI().getPath();
        } catch (Exception e) {
            return "";
        }
    }

    private String getLogFile() {
        try {
            ClassLoader classLoader = ScrollManager.class.getClassLoader();
            return classLoader.getResource(LOG_PATH).toURI().getPath();
        } catch (Exception e) {
            return "";
        }
    }

    private int loadScrollsFromFile() {
        String pathString = getScrollFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathString))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String name = parts[0];
                    String scrollId = parts[1];
                    String originalType = parts[2];
                    String uploaderId = parts[3];
                    String uploadDate = parts[4];
                    String downloadCount = parts[5];

                    Scroll scroll = new Scroll(name, scrollId, originalType, uploaderId, uploadDate, downloadCount);
                    scrollsByScrollId.put(scrollId, scroll);
                }
            }
            return 0;
        } catch (IOException e) {
            System.out.println("No scroll data file found. Please inform admin.");
                return 1;
        }
    }

    // Add new scroll
    public void addScroll(Scanner scanner, User currentUser) {
        try {
            String scrollName;
            File saveFile;
            while (true) {
                // Step 1: Ask for the scroll name
                System.out.print("Enter the name of the scroll, or cancel to escape: ");
                scrollName = scanner.nextLine().trim();
                if (scrollName.length() == 0) {
                    System.out.println("You can not leave this field blank.");
                    continue;
                }
                if (scrollName.equalsIgnoreCase("cancel")) {
                    return;
                }
                String saveFilePath = getScrollResourcePath(scrollName);
                saveFile = new File(saveFilePath);
                if (saveFile.exists()) {
                    System.out.println("Sorry, file already exists. Please enter a different name.");
                } else  {
                    break;
                }
            }

            String scrollId;
            // Step 3: Generate a unique scroll ID
            while (true) {
                scrollId = UUID.randomUUID().toString();
                if (!scrollsByScrollId.containsKey(scrollId)) {
                    break;
                }
            }

            // // Step 2: Ask for the content to be saved in the scroll
            // System.out.print("Enter the content of the scroll: ");
            // String scrollContent = scanner.nextLine().trim();

            File locFile;
            while (true) {
                System.out.print("Enter the file path of the scroll to upload, or cancel to escape: ");
                String locName = scanner.nextLine().trim();
                if (locName.length() == 0) {
                    System.out.println("You can not leave this field blank.");
                    continue;
                }
                if (locName.equalsIgnoreCase("cancel")) {
                    return;
                }
                locFile = new File(locName);
                if (locFile.exists() && locFile.isFile()) {
                    if (currentUser.getUserType().equals("Guest")) {
                        BufferedReader checkTen = new BufferedReader(new FileReader(locFile));
                        int lineCount = 0;
                        while (checkTen.readLine() != null) {
                            lineCount++;
                        }
                        checkTen.close();
                        if (lineCount > 10) {
                            System.out.println("Sorry, file is too long to upload as a guest. Please log in / sign up if you wish to upload a file longer than 10 lines.");
                            continue;
                        }
                    }
    
                    try (InputStream in = new FileInputStream(locFile); OutputStream out = new FileOutputStream(saveFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        String extension = locName.substring(locName.lastIndexOf("."));
                        Scroll newScroll = new Scroll(scrollName, scrollId, extension, currentUser.getIdKey());
                        scrollsByScrollId.put(scrollId, newScroll);

                        // Step 6: Optionally, save scroll metadata to a file (like scrolls.txt)
                        saveNewScrollMetadataToFile(newScroll);

                        System.out.println("Scroll added successfully with ID: " + scrollId);
                        appendLogFile(currentUser, "added Scroll " + scrollId);
                        return;
                    } catch (IOException e) {
                        System.out.println("Error uploading the scroll: " + e.getMessage());
                    }
                } else {
                    System.out.println("Scroll file not found.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading or saving the file.");
        }
    }

    public void updateScroll(Scanner scanner, User currentUser) {
        List<Scroll> filteredScrolls = new ArrayList<>(scrollsByScrollId.values());
        if (!currentUser.getUserType().equals("Admin")) {
            //get user's scrolls
            filteredScrolls.removeIf(scroll -> !scroll.getUploaderId().equals(currentUser.getIdKey()));
        }

        System.out.println("Here is a list of all scrolls you can modify. ");
        while (true) {
            for (int i = 0; i < filteredScrolls.size(); i++) {
                Scroll scroll = filteredScrolls.get(i);
                System.out.println((i + 1) + ". " + scroll.viewInfo());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    continue;
                }    
            }
            System.out.println("Enter the number of scroll would you like to rename, or 'cancel' to escape: ");
            String scrollToEdit = scanner.nextLine().trim();
            if (scrollToEdit.equalsIgnoreCase("cancel")) {
                return;
            }
            else {
                try {
                    int index = Integer.parseInt(scrollToEdit) - 1;
                    if (index >= 0 && index < filteredScrolls.size()) {
                        Scroll scroll = filteredScrolls.get(index);
                        String newName;
                        File oldFile = new File(getScrollResourcePath(scroll.getName()));
                        String oldName = scroll.getName();
                        File newFile;
                        while (true) {
                            System.out.println("What would you like to rename the scroll to, or 'cancel' to escape: ");
                            newName = scanner.nextLine().trim();
                            if (newName.length() == 0) {
                                System.out.println("You can not leave this field blank.");
                                continue;
                            }
                            if (newName.equalsIgnoreCase("cancel")) {
                                return;
                            }
                            newFile = new File(getScrollResourcePath(newName));
                            if (newFile.exists()) {
                                System.out.println("Name already taken.");
                            } else {
                                break;
                            }
                        }
                        if (!oldFile.renameTo(newFile)) {
                            System.out.println("Error renaming file.");
                            return;
                        }
                        scroll.setName(newName);
                        saveScrollMetadataToFile();
                        appendLogFile(currentUser, "renamed Scroll " + scroll.getScrollId() + " from " + oldName + " to " + newName);
                        System.out.println("Would you like to rename any more scrolls? (yes|no) ");
                        String response = scanner.nextLine().trim();
                        if (response.equalsIgnoreCase("yes")) {
                            continue;
                        } else {
                            return;
                        }
                    } else {
                        System.out.println("Invalid scroll number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Scroll number is the number on the left. Please enter a valid number.");
                } catch (Exception e) {
                    System.out.println("Error in update. Please inform an administrator.");
                }
            }
        }
    }

    public void deleteScroll(Scanner scanner, User currentUser) {
        List<Scroll> filteredScrolls = new ArrayList<>(scrollsByScrollId.values());
        if (!currentUser.getUserType().equals("Admin")) {
            //get user's scrolls
            filteredScrolls.removeIf(scroll -> !scroll.getUploaderId().equals(currentUser.getIdKey()));
        }

        System.out.println("Here is a list of all scrolls you can delete. ");
        while (true) {
            for (int i = 0; i < filteredScrolls.size(); i++) {
                Scroll scroll = filteredScrolls.get(i);
                System.out.println((i + 1) + ". " + scroll.viewInfo());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    continue;
                }    
            }
            System.out.println("Enter the number of scroll would you like to delete, or 'cancel' to escape: ");
            String scrollToEdit = scanner.nextLine().trim();
            if (scrollToEdit.equalsIgnoreCase("cancel")) {
                return;
            }
            else {
                try {
                    int index = Integer.parseInt(scrollToEdit) - 1;
                    if (index >= 0 && index < filteredScrolls.size()) {
                        Scroll scroll = filteredScrolls.get(index);
                        System.out.println("Are you sure you want to remove " + scroll.getName() + " from the system? (yes|no)");
                        String confirmation = scanner.nextLine().trim();
                        if (confirmation.equalsIgnoreCase("yes")) {
                            File scrollFile = new File(getScrollResourcePath(scroll.getName()));
                            String idGone = scroll.getScrollId();
                            scrollFile.delete();
                            scrollsByScrollId.remove(scroll.getScrollId());
                            saveScrollMetadataToFile();
                            appendLogFile(currentUser, "deleted Scroll " + idGone);
                            System.out.println("Scroll deleted successfully.");
                        } else {
                            System.out.println("Delete operation cancelled for scroll " + scroll.getName() + ".");
                        }
                        System.out.println("Would you like to delete any more scrolls? (yes|no) ");
                        String response = scanner.nextLine().trim();
                        if (response.equalsIgnoreCase("yes")) {
                            continue;
                        } else {
                            return;
                        }
                    } else {
                        System.out.println("Invalid scroll number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Scroll number is the number on the left. Please enter a valid number.");
                } catch (Exception e) {
                    System.out.println("Error in delete. Please inform an administrator.");
                }
            }
        }
    }
    
    public void showAllStats() {
        System.out.println("There are " + scrollsByScrollId.size() + " scrolls in the system.");
        for (Scroll scroll : scrollsByScrollId.values()) {
            System.out.println("Scroll: " + scroll.getScrollId() + ", Download Count: " + scroll.getDownloadCount());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                continue;
            }    
        }
        return;
    }
}