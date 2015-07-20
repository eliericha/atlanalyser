/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;

public class SyncEList<E> implements EList<E> {

    private EList<E> list;

    public SyncEList(EList<E> list) {
        this.list = list;
    }

    @Override
    synchronized public int size() {
        return list.size();
    }

    @Override
    synchronized public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    synchronized public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    synchronized public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    synchronized public Object[] toArray() {
        return list.toArray();
    }

    @Override
    synchronized public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    synchronized public boolean add(E e) {
        return list.add(e);
    }

    @Override
    synchronized public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    synchronized public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    synchronized public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    @Override
    synchronized public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(c);
    }

    @Override
    synchronized public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    synchronized public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    synchronized public void clear() {
        list.clear();
    }

    @Override
    synchronized public E get(int index) {
        return list.get(index);
    }

    @Override
    synchronized public E set(int index, E element) {
        return list.set(index, element);
    }

    @Override
    synchronized public void add(int index, E element) {
        list.add(index, element);
    }

    @Override
    synchronized public E remove(int index) {
        return list.remove(index);
    }

    @Override
    synchronized public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    synchronized public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    synchronized public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    @Override
    synchronized public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    synchronized public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    synchronized public void move(int newPosition, E object) {
        list.move(newPosition, object);
    }

    @Override
    synchronized public E move(int newPosition, int oldPosition) {
        return list.move(newPosition, oldPosition);
    }

}