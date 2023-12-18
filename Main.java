import java.text.Collator;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        // Příklad seznamu uživatelů
        List<User> users = List.of(
                new User("John"),
                new User("AlicABCABCe"),
                new User("Žaneta"),
                new User("Zuneta"),
                new User("zuneta"),
                new User("Bob")
        );

        Comparator<User> comparator = Comparator.comparing(user -> removeDiacritics(user.getName().toLowerCase(Locale.ENGLISH)));


        // Vytvoření komparátoru s ignorováním diakritiky a bez ohledu na velikost písmen
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        collator.setStrength(Collator.NO_DECOMPOSITION);

        // Seřazení uživatelů podle jména s použitím komparátoru
        var sortedUsers = users.stream()
                //.sorted(comparator)
                .map(user -> removeDiacritics(user.name))
                .collect(Collectors.toList());

        // Výpis seřazených uživatelů
        sortedUsers.forEach(user -> System.out.println(user));
    }

    static String removeDiacritics(String input) {
        var normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replaceAll("[\\-\\+\\.\\|:;,_/ ]+", " ");
        return normalized;
    }

    // Entita User
    static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
