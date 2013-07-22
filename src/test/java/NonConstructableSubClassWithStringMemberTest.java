import org.junit.Test;

import static java.lang.Integer.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class NonConstructableSubClassWithStringMemberTest {
    @Test
    public void shouldCreateSubClassInstanceWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClassWithStringMember instance =
                NonConstructableSubClassWithStringMember.newInstance(5);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(7));
        assertThat(valueOf(instance.getFieldC()), is(17));
        assertTrue(instance.getMemberClass() == String.class);
    }

    @Test
    public void shouldCreateSubClassInstanceWithConstructor() throws NoSuchMethodException {
        final NonConstructableSubClassWithStringMember instance =
                NonConstructableSubClassWithStringMember.newInstance(5, 9, 21);
        assertThat(valueOf(instance.getFieldA()), is(5));
        assertThat(valueOf(instance.getFieldB()), is(9));
        assertThat(valueOf(instance.getFieldC()), is(21));
        assertTrue(instance.getMemberClass() == String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithDefaultParams() throws NoSuchMethodException {
        final NonConstructableSubClassWithStringMember instance =
                new NonConstructableSubClassWithStringMember(new Object(), String.class, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToConstructWithParams() throws NoSuchMethodException {
        final NonConstructableSubClassWithStringMember instance =
                new NonConstructableSubClassWithStringMember(new Object(), String.class, 5, 9, 21);
    }

}
