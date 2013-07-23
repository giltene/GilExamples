import org.junit.Test;

import static java.lang.Integer.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NonConstructableSubClassTest {
    @Test
    public void shouldCreateSubClassInstanceWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClass instance =
                NonConstructableSubClass.newInstance(5);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(7));
        assertThat(valueOf(instance.getFieldC()), is(17));

    }

    @Test
    public void shouldCreateSubClassInstanceWithConstructor() throws NoSuchMethodException {
        final NonConstructableSubClass instance =
                NonConstructableSubClass.newInstance(5, 9, 21);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(9));
        assertThat(valueOf(instance.getFieldC()), is(21));
    }

    @Test
    public void shouldCreateBaseClassInstanceEvenWithSubclassNewInstance() throws NoSuchMethodException {
        final NonConstructableBaseClass instance =
                NonConstructableBaseClass.newInstance(NonConstructableBaseClass.class, 5);
        assertThat(valueOf(instance.getFieldA()), is(5));
    }

    @Test
    public void shouldCreateBaseClassInstance() throws NoSuchMethodException {
        final NonConstructableBaseClass instance =
                NonConstructableBaseClass.newInstance(5);
        assertThat(valueOf(instance.getFieldA()), is(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClass instance =
                new NonConstructableSubClass(new Object(), 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithParams() throws NoSuchMethodException {
        final NonConstructableSubClass instance =
                new NonConstructableSubClass(new Object(), 5, 9, 21);
    }

}
