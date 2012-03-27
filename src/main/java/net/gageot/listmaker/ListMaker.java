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

import com.google.common.base.*;
import com.google.common.collect.*;

import javax.annotation.*;
import java.util.*;

import static com.google.common.base.Preconditions.*;

/**
 * ListMaker is a fluent interface list maker for use with Guava.<br/> It makes it easy to start from an {@link
 * Iterable} and apply transformations, filtering and operations on it. Any of these can be combined.
 *
 * @param <T> the type of elements returned by the iterator.
 * @author David Gageot
 * @since 1.0
 */
public final class ListMaker<T> implements Iterable<T> {
	private final Iterable<T> values;

	private ListMaker(Iterable<T> values) {
		checkNotNull(values);
		this.values = values;
	}

	/**
	 * Creates a ListMaker from an {@link Iterable}.
	 * <p/>
	 * <b>Note:</b> Trying to create a {@code ListMaker} from another
	 * {@code ListMaker} returns the original {@code ListMaker}.
	 *
	 * @return a new {@code ListMaker} or the {@code ListMaker} passed as {@code values}
	 */
	public static <T> ListMaker<T> with(Iterable<T> values) {
		checkNotNull(values);
		if (values instanceof ListMaker<?>) {
			return (ListMaker<T>) values;
		}
		return new ListMaker<T>(values);
	}

	/**
	 * Creates an empty {@code ListMaker}.
	 *
	 * @return an empty {@code ListMaker}
	 */
	@SuppressWarnings("unchecked")
	public static <T> ListMaker<T> with() {
		return new ListMaker<T>(Collections.<T>emptyList());
	}

	/**
	 * Creates a {@code ListMaker} from a list of values.
	 *
	 * @return a new {@code ListMaker}
	 */
	public static <T> ListMaker<T> with(T... values) {
		checkNotNull(values);
		return new ListMaker<T>(Arrays.asList(values));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public static <V, T> Predicate<? super T> where(Function<? super T, ? extends V> transform, Predicate<? super V> predicate) {
		checkNotNull(transform);
		checkNotNull(predicate);
		return Predicates.compose(predicate, transform);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public static <V, T> Predicate<? super T> whereEquals(Function<? super T, ? extends V> transform, @Nullable V valueToCompareWith) {
		checkNotNull(transform);
		return where(transform, Predicates.equalTo(valueToCompareWith));
	}

    /**
	 * Returns a filtered {@code ListMaker} that keeps only the elements that satisfy a {@code predicate}.
	 *
     * @param predicate the predicate to satisfy to be included
     * @return a filtered {@code ListMaker}
	 */
	public ListMaker<T> only(Predicate<? super T> predicate) {
		checkNotNull(predicate);
		return new ListMaker<T>(Iterables.filter(values, predicate));
	}

    /**
     * Returns a filtered {@code ListMaker} that exclude the elements that satisfy a {@code predicate}.
     *
     * @param predicate the predicate to satisfy to be excluded
     * @return a filtered {@code ListMaker}
     */
    public ListMaker<T> exclude(Predicate<? super T> predicate) {
		checkNotNull(predicate);
		return only(Predicates.not(predicate));
	}

    /**
     * Returns a filtered {@code ListMaker} that excludes given {@code values}.
     *
     * @param values the values to exclude
     * @return a filtered {@code ListMaker}
     */
    public ListMaker<T> exclude(T... values) {
		checkNotNull(values);
		return exclude(Arrays.asList(values));
	}

    /**
     * Returns a filtered {@code ListMaker} that excludes given collection of {@code values}.
     *
     * @param values the collection of values to exclude
     * @return a filtered {@code ListMaker}
     */
    public ListMaker<T> exclude(Collection<? extends T> values) {
		checkNotNull(values);
		return exclude(Predicates.in(values));
	}

	/**
	 * Returns the first element in the {@code ListMaker}.
	 *
	 * @return the first element in the {@code ListMaker}
	 * @throws NoSuchElementException if the {@code ListMaker} is empty
	 */
	public T first() {
		return values.iterator().next();
	}

	/**
	 * Returns the first element in the {@code ListMaker} that satisfies the given {@code predicate}.
	 *
	 * @param predicate the predicate to satisfy
	 * @return the first element in the {@code ListMaker} that satisfies the {@code predicate}
	 * @throws NoSuchElementException if no element satisfies the {@code predicate}
	 */
	public T first(Predicate<? super T> predicate) {
		checkNotNull(predicate);
		return Iterables.find(values, predicate);
	}

	/**
	 * Returns the first element in the {@code ListMaker} or {@code defaultValue}
	 * if the {@code ListMaker} is empty.
	 *
	 * @param defaultValue the default value to return if the {@code ListMaker} is empty
	 * @return the first element in the {@code ListMaker} or the default value
	 */
	public T firstOrDefault(@Nullable T defaultValue) {
		return Iterables.getFirst(values, defaultValue);
	}

	/**
	 * Returns the first element in the {@code ListMaker} that satisfies the given {@code predicate}
	 * or {@code defaultValue} if no element satisfies the {@code predicate}.
	 *
	 * @param predicate    the predicate to satisfy
	 * @param defaultValue the default value to return if no element satisfies the {@code predicate}
	 * @return the first element in the {@code ListMaker} that satisfies the {@code predicate} or the default value
	 */
	public T firstOrDefault(Predicate<? super T> predicate, @Nullable T defaultValue) {
		checkNotNull(predicate);
		return Iterables.find(values, predicate, defaultValue);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public boolean contains(Predicate<? super T> predicate) {
		checkNotNull(predicate);
		return Iterables.any(values, predicate);
	}

    /**
     * Returns the number of elements in the {@code ListMaker} that satisfies the given {@code predicate}.
     *
     * @param predicate    the predicate to satisfy
     * @return the number of elements in the {@code ListMaker} that satisfies the {@code predicate}
     */
    public int count(Predicate<? super T> predicate) {
		checkNotNull(predicate);
		return Iterables.size(Iterables.filter(values, predicate));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public ListMaker<T> sortOn(Ordering<? super T> ordering) {
		return new ListMaker<T>(ordering.sortedCopy(values));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public ListMaker<T> sortOn(Comparator<? super T> comparator) {
		checkNotNull(comparator);
		return sortOn(Ordering.from(comparator));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public ListMaker<T> sortOn(Function<? super T, ? extends Comparable<?>> transform) {
		checkNotNull(transform);
		return sortOn(Ordering.natural().onResultOf(transform));
	}

    /**
     * Returns a filtered {@code ListMaker} that excludes {@code null} values.
     *
     * @return a filtered {@code ListMaker}
     */
    public ListMaker<T> notNulls() {
		return only(Predicates.<T>notNull());
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R> ListMaker<R> to(Function<? super T, R> transform) {
		checkNotNull(transform);
		return new ListMaker<R>(Iterables.transform(values, transform));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R, C extends Iterable<R>> ListMaker<R> flatMap(Function<? super T, C> transform) {
		checkNotNull(transform);
		return new ListMaker<R>(Iterables.concat(Iterables.transform(values, transform)));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public T max(Ordering<? super T> ordering) {
		return ordering.max(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public T min(Ordering<? super T> ordering) {
		return ordering.min(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <V extends Comparable<V>> T maxOnResultOf(Function<? super T, V> transform) {
		checkNotNull(transform);
		return Ordering.natural().onResultOf(transform).max(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <V extends Comparable<V>> T minOnResultOf(Function<? super T, V> transform) {
		checkNotNull(transform);
		return Ordering.natural().onResultOf(transform).min(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <C extends Collection<T>> C copyTo(C destination) {
		checkNotNull(destination);
		Iterables.addAll(destination, values);
		return destination;
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public List<T> toList() {
		return Lists.newArrayList(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public ImmutableList<T> toImmutableList() {
		return ImmutableList.copyOf(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public String join(@Nullable String separator) {
		return Joiner.on(separator).join(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public T[] toArray(Class<T> type) {
		checkNotNull(type);
		return Iterables.toArray(values, type);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public Set<T> toSet() {
		return Sets.newHashSet(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R> Set<R> toSet(Function<? super T, R> transform) {
		checkNotNull(transform);
		return Sets.newHashSet(Iterables.transform(values, transform));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public TreeSet<T> toTreeSet() {
		return copyTo(new TreeSet<T>());
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public TreeSet<T> toTreeSet(Comparator<? super T> comparator) {
		checkNotNull(comparator);
		return copyTo(new TreeSet<T>(comparator));
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R> TreeSet<R> toTreeSet(Function<? super T, R> transform, Comparator<? super R> ordering) {
		checkNotNull(transform);
		TreeSet<R> set = new TreeSet<R>(ordering);
		Iterables.addAll(set, Iterables.transform(values, transform));
		return set;
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R extends Comparable<R>> TreeSet<R> toTreeSet(Function<? super T, R> transform) {
		checkNotNull(transform);
		return toTreeSet(transform, null);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public ImmutableSet<T> toImmutableSet() {
		return ImmutableSet.copyOf(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <R> ImmutableSet<R> toImmutableSet(Function<? super T, R> transform) {
		checkNotNull(transform);
		return ImmutableSet.copyOf(Iterables.transform(values, transform));
	}

    /**
     * Returns the last element of the {@code ListMaker}.
     *
     * @return the last element of the {@code ListMaker}
     * @throws NoSuchElementException if the {@code ListMaker} is empty
     */
    public T getLast() {
		return Iterables.getLast(values);
	}

    /**
     * Returns the number of elements in the {@code ListMaker}.
     *
     * @return the number of elements
     */
    public int size() {
		return Iterables.size(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public <K> Map<K, T> indexBy(Function<? super T, ? extends K> toKey) {
		checkNotNull(toKey);
		Map<K, T> map = Maps.newHashMap();

		for (T value : values) {
			map.put(toKey.apply(value), value);
		}

		return map;
	}

	/**
	 * Returns an iterator over a set of elements of type T.
	 *
	 * @return an {@link Iterator}
	 */
	public Iterator<T> iterator() {
		return values.iterator();
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	public boolean isEmpty() {
		return Iterables.isEmpty(values);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof Iterable<?> && Iterables.elementsEqual(this, (Iterable<?>) o);
	}

	/**
	 * TODO.
	 *
	 * @return TODO.
	 */
	@Override
	public int hashCode() {
		return values.hashCode();
	}

    /**
     * Returns a string representation of the {@code ListMaker} elements, with the format
     * {@code [e1, e2, ..., en]}.
     *
     * @return a string representation of the {@code ListMaker} elements
     */
    @Override
	public String toString() {
		return values.toString();
	}
}
