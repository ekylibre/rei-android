package ekylibre.util.antlr4;

import android.util.Log;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ekylibre.util.antlr4.QueryLanguageParser.AbilityContext;
import ekylibre.util.ontology.Ontology;
import ekylibre.zero.inter.model.GenericItem;

public class Grammar {

    private static final String TAG = "Grammar";

    public static List<GenericItem> getFilteredItems(String procedureFilter, List<GenericItem> items, String search) {

        List<GenericItem> list = new ArrayList<>();

        // Prepare required abilities variants
        List<List<String>> requiredAbilities = computeAbilitiesPhrase(procedureFilter);

        Log.e(TAG, "Mandatory abilities --> " + requiredAbilities);

        // Loop over all available items
        itemLoop: for (GenericItem item : items) {
            if (item.abilities != null) {

                // Check all procedure mandatory abilities one by one
                abilityLoop: for (List<String> extendedAbility : requiredAbilities) {

                    for (String ability : item.abilities)
                        if (extendedAbility.contains(ability))
                            continue abilityLoop;

                    // If here reached, none of current extended ability variant match, skip item
                    continue itemLoop;
                }

                if (search != null) {

                    // Normalizes search string
                    String filterText = StringUtils.stripAccents(search.toLowerCase());

                    // Search in name
                    String name = StringUtils.stripAccents(item.name.toLowerCase());
                    if (name.contains(filterText)) {
                        list.add(item);
                        continue;
                    }

                    // Search in number if not null
                    if (item.number != null) {
                        String number = StringUtils.stripAccents(item.number.toLowerCase());
                        if (number.contains(filterText)) {
                            list.add(item);
                            continue;
                        }
                    }

                    // Search in workNumber if not null
                    if (item.workNumber != null) {
                        String workNumber = StringUtils.stripAccents(item.workNumber.toLowerCase());
                        if (workNumber.contains(filterText))
                            list.add(item);
                    }

                } else {
                    list.add(item);
                }
            }
        }

        // Return the selected items after browsing all items
        return list;
    }

    private static List<List<String>> computeAbilitiesPhrase(String input) {  //boolean getLowerAbilities

        List<List<String>> output = new ArrayList<>();

        QueryLanguageLexer lexer = new QueryLanguageLexer(CharStreams.fromString(input));
        QueryLanguageParser parser = new QueryLanguageParser(new CommonTokenStream(lexer));
        ParseTreeWalker.DEFAULT.walk(new QueryLanguageBaseListener() {

            @Override
            public void enterEssence(QueryLanguageParser.EssenceContext ctx) {
                super.enterEssence(ctx);

                output.add(Collections.singletonList(ctx.getText()));

            }

            @Override
            public void enterAbility(AbilityContext ctx) {
                super.enterAbility(ctx);

                String ability = ctx.ability_name().name().getText();
                List<String> extendedVariants = new ArrayList<>();

                if (ctx.ability_parameters() != null) {

                    String abilityParameter = ctx.ability_parameters().ability_argument().get(0).getText();
                    List<String> parameterVariants = Ontology.findParentsInRealm(abilityParameter);

                    for (String param : parameterVariants)
                        extendedVariants.add("can " + ability + "(" + param + ")");

                    output.add(extendedVariants);

                } else {
                    output.add(Collections.singletonList("can " + ability));
                }
            }
        }, parser.boolean_expression());

        return output;
    }

    public static List<String> computeItemAbilities(String input) {  //boolean getLowerAbilities

        List<String> output = new ArrayList<>();

        QueryLanguageLexer lexer = new QueryLanguageLexer(CharStreams.fromString(input));
        QueryLanguageParser parser = new QueryLanguageParser(new CommonTokenStream(lexer));
        ParseTreeWalker.DEFAULT.walk(new QueryLanguageBaseListener() {

            @Override
            public void enterEssence(QueryLanguageParser.EssenceContext ctx) {
                super.enterEssence(ctx);

                List<String> varietyParents = Ontology.findParentsInRealm(ctx.variety_name().getText());

                for (String variety : varietyParents)
                    output.add("is " + variety);
            }

        }, parser.boolean_expression());

        return output;
    }
}