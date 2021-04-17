package com.septech.centauri.ui.user.listing;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.septech.centauri.R;
import com.septech.centauri.domain.models.Business;
import com.septech.centauri.domain.models.Item;
import com.septech.centauri.domain.models.ItemReview;
import com.septech.centauri.domain.models.Order;
import com.septech.centauri.ui.user.home.HomeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListingFragment extends Fragment {
    private ListingViewModel mViewModel;
    private HomeViewModel mHomeViewModel;

    private RecyclerView listingRV;
    private ReviewAdapter adapter;

    private ConstraintLayout layout;

    private ImageView itemImage;

    private ExtendedFloatingActionButton wishlistBtn;
    private ExtendedFloatingActionButton cartBtn;

    private ImageButton backBtn;
    private ImageButton imageBackBtn;
    private ImageButton imageForwardBtn;
    private ImageButton quantityBackBtn;
    private ImageButton quantityForwardBtn;

    private Button businessProfileBtn;

    private TextView listingNameTextView;
    private TextView listingPriceTextView;
    private TextView listingDescTextView;
    private TextView listingRatingScore;
    private TextView quantityLeftTextView;

    private EditText quantityEditText;

    private RatingBar listingRatingBar;

    private Spinner listingSpinner;

    public static ListingFragment newInstance() {
        return new ListingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_listing_fragment, container, false);

        layout = view.findViewById(R.id.listing_layout);
        layout.setVisibility(View.GONE);

        itemImage = view.findViewById(R.id.listingImageView);

        wishlistBtn = view.findViewById(R.id.wishlistBtn);
        cartBtn = view.findViewById(R.id.cartBtn);

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            System.out.println("v = " + v);
            getActivity().onBackPressed();
        });

        imageBackBtn = view.findViewById(R.id.imageBackBtn);
        imageForwardBtn = view.findViewById(R.id.imageForwardBtn);

        quantityBackBtn = view.findViewById(R.id.quantityBackBtn);
        quantityForwardBtn = view.findViewById(R.id.quantityForwardBtn);

        businessProfileBtn = view.findViewById(R.id.businessProfileBtn);

        listingNameTextView = view.findViewById(R.id.listingNameTextView);
        listingPriceTextView = view.findViewById(R.id.listingPriceTextView);
        listingDescTextView = view.findViewById(R.id.listingDescTextView);
        listingRatingScore = view.findViewById(R.id.listingRatingScore);
        quantityLeftTextView = view.findViewById(R.id.quantityLeftTextView);

        quantityEditText = view.findViewById(R.id.quantityEditText);

        //find spinner and set item listener
        listingSpinner = view.findViewById(R.id.listingSpinner);
        listingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("parent = " + parent + ", view = " + view + ", position = " + position + ", id = " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("parent = " + parent);
            }
        });

        //create spinner choices
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_list_item_1,
                Arrays.asList("Most Recent", "Least Recent", "Highest Rated", "Lowest Rated",
                        "Most Helpful"));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        listingSpinner.setAdapter(dataAdapter);

        listingRatingBar = view.findViewById(R.id.listingRatingBar);

        listingRV = view.findViewById(R.id.listingRV);

        adapter = new ReviewAdapter(new ArrayList<>());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        mViewModel.setItemId(getArguments().getInt("id"));

        createLiveDataObservers();
    }

    private void createLiveDataObservers() {
        mViewModel.getItemLiveData().observe(getViewLifecycleOwner(), item -> {
            Resources res = requireActivity().getResources();

            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("v = " + v);
                }
            });

            cartBtn.setOnClickListener(v -> {
                int quantity = mViewModel.getCurrentQuantity();

                if (quantity == 0) {
                    //TODO show toast message
                    return;
                }

                mViewModel.addToCart(mHomeViewModel.getUserLiveData().getValue(), item, quantity);
            });

            listingNameTextView.setText(item.getName());

            String COUNTRY = "US";
            String LANGUAGE = "en";

            listingPriceTextView.setText(item.getDisplayablePrice());
            listingDescTextView.setText(item.getDescription());

            mViewModel.setCurrentQuantity(0);
            quantityEditText.setText(String.valueOf(mViewModel.getCurrentQuantity()));
            updateQuantityBtnState(item.getQuantity());

            quantityBackBtn.setOnClickListener(v -> {
                mViewModel.setCurrentQuantity(mViewModel.getCurrentQuantity() - 1);
                quantityEditText.setText(String.valueOf(mViewModel.getCurrentQuantity()));
                updateQuantityBtnState(item.getQuantity());
            });

            quantityForwardBtn.setOnClickListener(v -> {
                mViewModel.setCurrentQuantity(mViewModel.getCurrentQuantity() + 1);
                quantityEditText.setText(String.valueOf(mViewModel.getCurrentQuantity()));
                updateQuantityBtnState(item.getQuantity());
            });

            quantityLeftTextView.setText(res.getString((R.string.listingQuantityLeft),
                    item.getQuantity()));

            //set rating bar and score

            //add reviews adapter

            layout.setVisibility(View.VISIBLE);
        });

        mViewModel.getBusinessLiveData().observe(getViewLifecycleOwner(), business -> {
            businessProfileBtn.setText(requireActivity().getResources().getString((R.string.business_profile_text),
                    business.getBusinessName()));

            businessProfileBtn.setOnClickListener(v -> {
                System.out.println("v = " + v);
            });
        });

        mViewModel.getImageLiveData().observe(getViewLifecycleOwner(), uris -> {
            mViewModel.setCurrentImage(0);
            itemImage.setImageURI(uris.get(mViewModel.getCurrentImage()));

            updateImageBtnState(uris);

            imageBackBtn.setOnClickListener(v -> {
                mViewModel.setCurrentImage(mViewModel.getCurrentImage() - 1);
                itemImage.setImageURI(uris.get(mViewModel.getCurrentImage()));

                updateImageBtnState(uris);
            });


            imageForwardBtn.setOnClickListener(v -> {
                mViewModel.setCurrentImage(mViewModel.getCurrentImage() + 1);
                itemImage.setImageURI(uris.get(mViewModel.getCurrentImage()));

                updateImageBtnState(uris);
            });
        });

        mViewModel.getReviews().observe(getViewLifecycleOwner(),
                itemReviews -> System.out.println("itemReviews = " + itemReviews));

        mViewModel.getOrderLiveData().observe(getViewLifecycleOwner(), new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                if (order == null) return;

                mHomeViewModel.updateOrderData(order);
                System.out.println("order = " + order);
            }
        });
    }

    private void updateImageBtnState(List<Uri> uris) {
        if (mViewModel.getCurrentImage() == uris.size() - 1) {
            imageForwardBtn.setVisibility(View.GONE);
            imageForwardBtn.setActivated(false);
        } else {
            imageForwardBtn.setVisibility(View.VISIBLE);
            imageForwardBtn.setActivated(true);
        }

        if (mViewModel.getCurrentImage() == 0) {
            imageBackBtn.setVisibility(View.GONE);
            imageBackBtn.setActivated(false);
        } else {
            imageBackBtn.setVisibility(View.VISIBLE);
            imageBackBtn.setActivated(true);
        }
    }

    private void updateQuantityBtnState(int quantity) {
        if (mViewModel.getCurrentQuantity() == quantity) {
            quantityForwardBtn.setVisibility(View.GONE);
            quantityForwardBtn.setActivated(false);
        } else {
            quantityForwardBtn.setVisibility(View.VISIBLE);
            quantityForwardBtn.setActivated(true);
        }

        if (mViewModel.getCurrentQuantity() == 0) {
            quantityBackBtn.setVisibility(View.GONE);
            quantityBackBtn.setActivated(false);
        } else {
            quantityBackBtn.setVisibility(View.VISIBLE);
            quantityBackBtn.setActivated(true);
        }
    }

    static class ReviewAdapter extends
            RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        private List<ItemReview> mReviews;

        public ReviewAdapter(List<ItemReview> mReviews) {
            this.mReviews = mReviews;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View itemView = inflater.inflate(R.layout.user_cart_item_fragment, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemReview review = mReviews.get(position);
        }

        public void setReviews(List<ItemReview> mReviews) {
            this.mReviews = mReviews;
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}