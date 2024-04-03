package org.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    record Player(String name, String id, Integer rating, Integer quickRating) {
    }

    public static void main(String[] args) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);

        HtmlPage searchPage = client.getPage("https://new.uschess.org/civicrm/player-search");
        HtmlForm form = searchPage.getFirstByXPath("//form");

        HtmlInput displayNameField = form.getInputByName("first_name");
        HtmlInput submitButton = form.getFirstByXPath("//button[@type='submit' and text()='Search']");

        displayNameField.type("Carlsen");
        HtmlPage resultsPage = submitButton.click();

        List<Player> players = parseResults(resultsPage);
        for (Player player : players) {
            System.out.println(player);
        }
    }

    private static List<Player> parseResults(HtmlPage resultsPage) {
        HtmlTable table = resultsPage.getFirstByXPath("//table[@class='civicrm-result-table']");
        List<Player> players = table.getBodies().get(0).getRows().stream()
                .map(r -> {
                    String rating = r.getCell(2).getTextContent();
                    String qrating = r.getCell(3).getTextContent();

                    return new Player(
                            r.getCell(0).getTextContent(),
                            r.getCell(1).getTextContent(),
                            rating.isEmpty() ? null : Integer.parseInt(rating),
                            qrating.isEmpty() ? null : Integer.parseInt(qrating)
                    );
                }).collect(Collectors.toList());

        return players;
    }
}
