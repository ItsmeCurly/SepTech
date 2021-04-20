package com.septech.centauri.ui.user.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.septech.centauri.R;
import com.septech.centauri.ui.user.help.HelpFragment;
import com.septech.centauri.ui.user.search.SearchFragment;
import com.septech.centauri.ui.user.cart.CartFragment;
import com.septech.centauri.ui.user.wishlist.WishlistFragment;


public class HomeFragment extends Fragment {

    private CallBackListener callBackListener;

    private ImageButton mViewItemsBtn;
    private ImageButton mCartBtn;
    private ImageButton mWishListBtn;
    private ImageButton mOrdersBtn;
    private ImageButton mViewHistoryBtn;
    private ImageButton mHelpBtn;

    private TextView mWelcomeMessage;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            callBackListener = (CallBackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CallBackListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_home_fragment, container, false);

        mViewItemsBtn = view.findViewById(R.id.homeViewAllItemsBtn);
        mCartBtn = view.findViewById(R.id.homeCartBtn);
        mWishListBtn = view.findViewById(R.id.homeWishListBtn);
        mOrdersBtn = view.findViewById(R.id.homeOrdersBtn);
        mViewHistoryBtn = view.findViewById(R.id.homeViewHistoryBtn);
        mHelpBtn = view.findViewById(R.id.homeNeedHelpBtn);
        mWelcomeMessage = view.findViewById(R.id.homeWelcomeTextView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewItemsBtn.setOnClickListener(v -> {
            SearchFragment fragment = SearchFragment.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString("query", "");
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentfragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        //TODO change button
        mCartBtn.setOnClickListener(v -> {
            CartFragment fragment = CartFragment.newInstance();

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentfragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        mWishListBtn.setOnClickListener(v -> {
            WishlistFragment fragment = WishlistFragment.newInstance();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentfragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        mOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("v = " + v);
            }
        });

        mViewHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mHelpBtn.setOnClickListener(v -> {
            HelpFragment fragment = HelpFragment.newInstance();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentfragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

}