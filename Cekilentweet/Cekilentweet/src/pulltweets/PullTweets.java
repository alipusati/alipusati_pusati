/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pulltweets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author ali betül
 */
public class PullTweets {

    List<String> list = new ArrayList<String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws TwitterException, FileNotFoundException, IOException {

        PullTweets pullTweets = new PullTweets();
        // TODO code application logic here
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("Rh7eY093xq5tnnd756XrkXnyv");
        cb.setOAuthConsumerSecret("B6O7JQC8RjdT5MAgwwDdc22vWCz1lirje0F274Z0oVYeqOf6p9");
        cb.setOAuthAccessToken("805689435350245376-blt8yGtMEA8li2oMcKEnVV4Zx2FDtMw");
        cb.setOAuthAccessTokenSecret("3uDDYnM8o5RA9gHVv4V975MioW0welyNJodGXsMbLrWEh");

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        System.out.println("Enter Twitter Username :");
        Scanner scanner = new Scanner(System.in);
        String user = scanner.nextLine();

        try {
            for (int i = 1; i < 5; i++) {
                Paging page = new Paging(i, 500);
                ResponseList<Status> response = twitter.getUserTimeline(user, page);
                if (response.isEmpty()) {
                    break;
                }
                for (int j = 0; j < response.size(); j++) {
                    Status status = response.get(j);
                    pullTweets.printBaseWords(status);
                }
            }
            System.out.println("Please select operation:\n1) Save\n2) Match");
        String operation = scanner.nextLine();

           while (true) {
               if (operation.equals("1")) {
                   //while (true) {
                 // System.out.println("Choose gender\n1) Male\n2) Female");
                  //    String gender = scanner.nextLine();
                   //   if (gender.equals("1")) {
                        pullTweets.saveWords(user.toString()+".txt");
                            break;
                     //  } else if (gender.equals("2")) {
                      //     pullTweets.saveWords(user.toString()+"Female.txt");
                       //    break;
                    } 
               else {
                        System.out.println("Wrong selection!!\nTry Again");
                    }
                    //}
                   // break;
        //        } 
        //else if (operation.equals("2")) 
          //      {
            //        pullTweets.matchWords();
             //       break;
          //      } 
           else {
                    System.out.println("Wrong selection!!\nTry Again");
                    System.out.println("Please select operation:\n1) Save\n2) Match");
                    operation = scanner.nextLine();
                }
            }
        } catch (TwitterException e) {
            System.err.println(e);
        }
    }

    private void printStatus(Status status) {
        System.out.println("@" + status.getUser().getScreenName());
        System.out.println(status.getText());
        System.out.println("");
    }

   private void printBaseWords(Status status) {
       Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());
        String words[] = status.getText().split(" ");
        for (String word : words) {
            try {
              String baseWord = zemberek.kelimeCozumle(word)[0].kok().icerik();
                if (getWordIndex(baseWord) == -1) {
                    if (baseWord.length() != 1) {
                        //System.out.println(baseWord);
                        list.add(baseWord + " 1");
                    }
                } else {
                    int index = getWordIndex(baseWord);
                    String containedWord[] = list.get(index).split(" ");
                    if (containedWord.length == 2) {
                        int count = Integer.parseInt(containedWord[1]);
                        list.remove(index);
                        list.add(index, baseWord + " " + ++count);
                    }
                }
            } catch (Exception e) {
            }

        }
    } 

    private boolean isStatusRT(Status status) {
        String statusText = status.getText();
        String words[] = statusText.split(" ");
        return words[0].equals("RT");
    }

    private int getWordIndex(String word) {
        for (int i = 0; i < list.size(); i++) {
            String temp[] = list.get(i).split(" ");
            if (temp[0].equals(word.split(" ")[0])) {
                return i;
            }
        }
        return -1;
    }

    private int getWordIndex(String word, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String temp[] = list.get(i).split(" ");
            if (temp[0].equals(word.split(" ")[0])) {
                return i;
            }
        }
        return -1;
    }

    private void saveWords(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            List<String> oldWords = new ArrayList<String>(Arrays.asList(everything.split("\r\n")));
            for (int i = 0; i < list.size(); i++) {
                String tempWord = list.get(i);
                int index = getWordIndex(tempWord, oldWords);
                if (index == -1) {
                    oldWords.add(tempWord);
                } else {
                    String containedWord[] = oldWords.get(index).split(" ");
                    int count = Integer.parseInt(containedWord[1]);
                    oldWords.remove(index);
                    oldWords.add(index, tempWord.split(" ")[0] + " " + ++count);
                }
            }
            File file = new File(filename);
            file.delete();
            for (int i = 0; i < oldWords.size(); i++) {
                file = new File(filename);
                FileWriter fw;
                try {
                    fw = new FileWriter(file, true);
                    fw.write("\r\n" + oldWords.get(i));
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (Exception exc) {
            for (int i = 0; i < list.size(); i++) {
                File dosya = new File(filename);
                FileWriter fw;
                try {
                    fw = new FileWriter(dosya, true);
                    fw.write("\r\n" + list.get(i));
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println("Successfully Saved :)");
    }

 /*   private void matchWords() {
       //int maleCount = getWordCount("\Male.txt");
        int femaleCount = getWordCount(user.toString()+".txt");
     //   int totalCount = maleCount + femaleCount;
      //  int persentageMale = 100 * maleCount / totalCount;
        int persentageFemale = 100 - persentageMale;
       // System.out.println("%" + persentageMale + " Male");
        System.out.println("%" + persentageFemale + " Female");
    }
*/
    private int getWordCount(String filename) {
        int wordCount = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            List<String> allWords = new ArrayList<String>(Arrays.asList(everything.split("\r\n")));
            for (int i = 0; i < list.size(); i++) {
                String tempWord = list.get(i);
                int index = getWordIndex(tempWord, allWords);
                if (index != -1) {
                    String containedWord[] = allWords.get(index).split(" ");
                    String countString = containedWord[1];
                    wordCount = Integer.parseInt(countString);
                }
            }
            br.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return wordCount;
    }

}
