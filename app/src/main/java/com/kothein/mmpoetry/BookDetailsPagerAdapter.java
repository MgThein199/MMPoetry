package com.kothein.mmpoetry;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.kothein.mmpoetry.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookDetailsPagerAdapter extends FragmentStateAdapter {

    private List<Book> books;
    private List<Fragment> fragments;
    private SparseArray<BookDetailsFragment> fragmentMap;

    public BookDetailsPagerAdapter(FragmentActivity fragmentActivity, List<Book> books) {
        super(fragmentActivity);
        this.books = books;
        this.fragments = new ArrayList<>();
        fragmentMap = new SparseArray<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create a new fragment for each book
        BookDetailsFragment fragment = BookDetailsFragment.newInstance(books.get(position));
        fragmentMap.put(position, fragment);
        fragments.add(fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public BookDetailsFragment getCurrentFragment(int position) {
        return fragmentMap.get(position);
    }
}
