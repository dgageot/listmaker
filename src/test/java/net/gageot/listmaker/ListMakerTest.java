/*
 * This file is part of ListMaker.
 *
 * Copyright (C) 2012
 * "David Gageot" <david@gageot.net>,
 *
 * ListMaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ListMaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ListMaker. If not, see <http://www.gnu.org/licenses/>.
 */
package net.gageot.listmaker;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;
import org.fest.assertions.Assertions;
import org.fest.assertions.BooleanAssert;
import org.fest.assertions.ListAssert;
import org.fest.assertions.MapAssert;
import org.fest.assertions.ObjectArrayAssert;
import org.fest.assertions.ObjectAssert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static net.gageot.listmaker.ListMaker.where;
import static net.gageot.listmaker.ListMaker.whereEquals;
import static net.gageot.listmaker.ListMaker.with;
import static org.fest.assertions.MapAssert.entry;

public class ListMakerTest {
  @Test
  public void canBuildFromList() {
    List<String> list = asList("A", "B", "C");

    ListMaker<String> iterable = ListMaker.with(list);

    assertThat(iterable).containsExactly("A", "B", "C");
  }

  @Test
  public void canBuildFromArray() {
    String[] array = {"C", "D", "E"};

    ListMaker<String> iterable = ListMaker.with(array);

    assertThat(iterable).containsExactly("C", "D", "E");
  }

  @Test
  public void canBuildFromVarArgs() {
    ListMaker<String> iterable = ListMaker.with("D", "E", "F", "G");

    assertThat(iterable).containsExactly("D", "E", "F", "G");
  }

  @Test
  public void canReturnSameListMakerAsTheOnePassed() {
    ListMaker<String> listMaker = ListMaker.with("A", "B", "C");

    ListMaker<String> listMakerOfListMaker = ListMaker.with(listMaker);

    assertThat((Object) listMakerOfListMaker).isSameAs(listMaker);
  }

  @Test
  public void canMakeList() {
    List<String> list = with("A", "B", "C").toList();

    assertThat(list).containsExactly("A", "B", "C");
  }

  @Test
  public void canMakeImmutableList() {
    ImmutableList<String> immutableList = with("A", "B", "C").toImmutableList();

    assertThat(immutableList).containsExactly("A", "B", "C");
  }

  @Test
  public void canMakeArray() {
    String[] array = with("A", "B", "C").toArray(String.class);

    assertThat(array).containsOnly("A", "B", "C");
  }

  @Test
  public void canMakeSet() {
    Set<String> set = with("A", "A", "B", "B", "C", "C").toSet();

    assertThat(set).containsOnly("A", "B", "C");
  }

  @Test
  public void canMakeSetUsingTransformation() {
    Set<String> set = with("A", "a", "B", "b", "C", "c").toSet(TO_UPPERCASE);

    assertThat(set).containsOnly("A", "B", "C");
  }

  @Test
  public void canMakeImmutableSet() {
    ImmutableSet<String> immutableSet = with("A", "A", "B", "B", "C", "C").toImmutableSet();

    assertThat(immutableSet).containsOnly("A", "B", "C");
  }

  @Test
  public void canMakeImmutableSetUsingTransformation() {
    ImmutableSet<String> immutableSet = with("A", "a", "B", "b", "C", "c").toImmutableSet(TO_UPPERCASE);

    assertThat(immutableSet).containsOnly("A", "B", "C");
  }

  @Test
  public void canMakeTreeSet() {
    TreeSet<String> treeSet = with("2", "3", "1", "1", "2", "3").toTreeSet();

    assertThat(treeSet).containsExactly("1", "2", "3");
  }

  @Test
  public void canMakeTreeSetUsingTransformation() {
    TreeSet<Integer> treeSet = with("2", "3", "1", "1", "2", "3").toTreeSet(TO_INTEGER);

    assertThat(treeSet).containsExactly(1, 2, 3);
  }

  @Test
  public void canMakeTreeSetWithOrdering() {
    Set<String> treeSet = with("2", "3", "1", "1", "2", "3").toTreeSet(Ordering.natural().reverse());

    assertThat(treeSet).containsExactly("3", "2", "1");
  }

  @Test
  public void canJoinValues() {
    String joined = with("A", "B", "C").join(":");

    assertThat(joined).isEqualTo("A:B:C");
  }

  @Test
  public void canKeepValuesBasedOnPredicate() {
    ListMaker<String> filtered = with("A", "B", "C").only(equalTo("B"));

    assertThat(filtered).containsExactly("B");
  }

  @Test
  public void canExcludeValuesBasedOnPredicate() {
    ListMaker<String> filtered = with("A", "B", "C").exclude(equalTo("B"));

    assertThat(filtered).containsExactly("A", "C");
  }

  @Test
  public void canKeepValuesBasedOnFunctionAndPredicate() {
    ListMaker<String> filtered = with("1", "2", "3").only(where(TO_INTEGER, equalTo(1)));

    assertThat(filtered).containsExactly("1");
  }

  @Test
  public void canKeepValuesBasedOnFunctionAndValue() {
    ListMaker<String> filtered = with("a", "b", "c").only(whereEquals(TO_UPPERCASE, "B"));

    assertThat(filtered).containsExactly("b");
  }

  @Test
  public void canExcludeValuesBasedOnFunctionAndPredicate() {
    ListMaker<String> filtered = with("1", "2", "3").exclude(where(TO_INTEGER, equalTo(1)));

    assertThat(filtered).containsExactly("2", "3");
  }

  @Test
  public void canExcludeOneValue() {
    ListMaker<String> filtered = with("A", "B", "C").exclude("A");

    assertThat(filtered).containsExactly("B", "C");
  }

  @Test
  public void canExcludeValueTwoValues() {
    ListMaker<String> filtered = with("A", "B", "C").exclude("A", "C");

    assertThat(filtered).containsExactly("B");
  }

  @Test
  public void canExcludeValues() {
    ListMaker<String> filtered = with("A", "B", "C").exclude(asList("A", "B"));

    assertThat(filtered).containsExactly("C");
  }

  @Test
  public void canKeepNonNullValues() {
    ListMaker<String> filtered = with("A", null, "C").notNulls();

    assertThat(filtered).containsExactly("A", "C");
  }

  @Test
  public void canTransformValues() {
    ListMaker<String> transformed = with(1, 2, 3).to(toStringFunction());

    assertThat(transformed).containsExactly("1", "2", "3");
  }

  @Test
  public void canGetFirst() {
    String first = with("A", "B", "C").first();

    assertThat(first).isEqualTo("A");
  }

  @Test
  public void canGetFirstWithPredicate() {
    String first = with("A", "B", "C").first(in(asList("B", "C")));

    assertThat(first).isEqualTo("B");
  }

  @Test(expected = NoSuchElementException.class)
  public void willFailToGetFirstElementOfEmptyList() {
    with().first();
  }

  @Test
  public void canUseADefaultValueVersionIfFirstIsNotFound() {
    assertThat(with().firstOrDefault("NOT FOUND")).isEqualTo("NOT FOUND");
    assertThat(with("FOUND").firstOrDefault("NOT FOUND")).isEqualTo("FOUND");

    assertThat(with().firstOrDefault(alwaysTrue(), "NOT FOUND")).isEqualTo("NOT FOUND");
    assertThat(with("FOUND").firstOrDefault(alwaysTrue(), "NOT FOUND")).isEqualTo("FOUND");
  }

  @Test
  public void canGetLast() {
    String last = with("A", "B", "C").getLast();

    assertThat(last).isEqualTo("C");
  }

  @Test(expected = NoSuchElementException.class)
  public void willFailToGetLastElementOfEmptyList() {
    with().getLast();
  }

  @Test
  public void canSort() {
    ListMaker<String> sorted = with("C", "B", "A").sortOn(Ordering.<String> natural());

    assertThat(sorted).containsExactly("A", "B", "C");
  }

  @Test
  public void canSortWithComparator() {
    ListMaker<String> sorted = with("C", "B", "A").sortOn(new Comparator<String>() {
      public int compare(String s1, String s2) {
        return s1.compareTo(s2);
      }
    });

    assertThat(sorted).containsExactly("A", "B", "C");
  }

  @Test
  public void canSortBaseOnTransformation() {
    ListMaker<String> sorted = with("90", "01", "300").sortOn(TO_INTEGER);

    assertThat(sorted).containsExactly("01", "90", "300");
  }

  @Test
  public void canCombineOperations() {
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    String juneToJuly = with(months).only(startsWith("J")).exclude("January").join(" to ");

    assertThat(juneToJuly).isEqualTo("June to July");
  }

  @Test
  public void canCountElements() {
    int count = with("C", "B", "A").size();

    assertThat(count).isEqualTo(3);
  }

  @Test
  public void canSearchValueWithPredicate() {
    String[] months = {"January", "February", "March"};

    boolean containsJanuary = with(months).contains(isEqualIgnoreCase("JANUARY"));
    boolean containsDecember = with(months).contains(isEqualIgnoreCase("DECEMBER"));

    assertThat(containsJanuary).isTrue();
    assertThat(containsDecember).isFalse();
  }

  @Test
  public void canFindMaximumValue() {
    int max = with(1, 15, 10).max(Ordering.<Integer> natural());

    assertThat(max).isEqualTo(15);
  }

  @Test
  public void canFindMaximumValueUsingTransformationFunction() {
    String max = with("0001", "15", "100").maxOnResultOf(TO_INTEGER);

    assertThat(max).isEqualTo("100");
  }

  @Test
  public void canFindMinimumValue() {
    int min = with(1, 15, 10).min(Ordering.<Integer> natural());

    assertThat(min).isEqualTo(1);
  }

  @Test
  public void canFindMinimumValueUsingTransformationFunction() {
    String min = with("0001", "15", "100").minOnResultOf(TO_INTEGER);

    assertThat(min).isEqualTo("0001");
  }

  @Test
  public void canCount() {
    int count = with("January", "February", "March").count(startsWith("J"));

    assertThat(count).isEqualTo(1);
  }

  @Test
  public void canIndexBy() {
    Map<Integer, String> byLength = with("1", "22", "333").indexBy(TO_LENGTH);

    assertThat(byLength) //
        .includes(entry(1, "1")) //
        .includes(entry(2, "22")) //
        .includes(entry(3, "333"));
  }

  @Test
  public void canGroupBy() {
    ListMultimap<Integer, String> byLength = with("1", "22", "333", "444").groupBy(TO_LENGTH);

    assertThat(byLength.asMap()) //
        .includes(entry(1, Arrays.asList("1"))) //
        .includes(entry(2, Arrays.asList("22"))) //
        .includes(entry(3, Arrays.asList("333", "444")));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void canFlatMap() {
    List<List<String>> listOfLists = asList(asList("1", "2"), asList("3", "4", "5"));

    ListMaker<String> values = with(listOfLists).flatMap(Functions.<Iterable<String>> identity());

    assertThat(values).containsExactly("1", "2", "3", "4", "5");
  }

  @Test
  public void canCheckEmptiness() {
    assertThat(with(newArrayList("1", "2", "1")).isEmpty()).isFalse();
    assertThat(with(newArrayList("1")).isEmpty()).isFalse();
    assertThat(with(newArrayList()).isEmpty()).isTrue();
  }

  private static <T> ListAssert assertThat(Iterable<T> actual) {
    return Assertions.assertThat(ImmutableList.copyOf(actual));
  }

  private static ObjectAssert assertThat(Object actual) {
    return Assertions.assertThat(actual);
  }

  private static BooleanAssert assertThat(boolean actual) {
    return Assertions.assertThat(actual);
  }

  private static ObjectArrayAssert assertThat(Object[] actual) {
    return Assertions.assertThat(actual);
  }

  public static MapAssert assertThat(Map<?, ?> actual) {
    return Assertions.assertThat(actual);
  }

  private static Function<String, Integer> TO_LENGTH = new Function<String, Integer>() {
    public Integer apply(String value) {
      return value.length();
    }
  };

  private static Function<String, Integer> TO_INTEGER = new Function<String, Integer>() {
    public Integer apply(String value) {
      return Integer.parseInt(value);
    }
  };

  private static Function<String, String> TO_UPPERCASE = new Function<String, String>() {
    public String apply(String value) {
      return value.toUpperCase();
    }
  };

  private static Predicate<String> startsWith(final String prefix) {
    return new Predicate<String>() {
      public boolean apply(String value) {
        return value.startsWith(prefix);
      }
    };
  }

  private static Predicate<String> isEqualIgnoreCase(final String expected) {
    return new Predicate<String>() {
      public boolean apply(String value) {
        return value.equalsIgnoreCase(expected);
      }
    };
  }
}
