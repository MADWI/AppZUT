package pl.edu.zut.mad.appzut.utils;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Locale;

/**
 * Rule to run unit test with all supported locales
 *
 * Usage:
 * {@code @Rule public WithAllLocalesRule mLocalesRule = new WithAllLocalesRule(); }
 */
class WithAllLocalesRule implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Locale defaultLocale = Locale.getDefault();
                try {
                    for (Locale locale : Locale.getAvailableLocales()) {
                        Locale.setDefault(locale);
                        base.evaluate();
                    }
                } finally {
                    Locale.setDefault(defaultLocale);
                }
            }
        };
    }
}
