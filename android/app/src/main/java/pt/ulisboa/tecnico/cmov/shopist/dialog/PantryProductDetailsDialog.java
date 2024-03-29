package pt.ulisboa.tecnico.cmov.shopist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pt.ulisboa.tecnico.cmov.shopist.PantryActivity;
import pt.ulisboa.tecnico.cmov.shopist.R;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.relations.PantryProduct;

public class PantryProductDetailsDialog extends DialogFragment {

    private final Context mContext;
    private View mDialogView;
    private final PantryProduct pantryProduct;

    public PantryProductDetailsDialog(Context context, PantryProduct pantryProduct) {
        this.mContext = context;
        this.pantryProduct = pantryProduct;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme_FullScreen);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_pantry_product_details, null);


        alertDialogBuilder.setTitle(R.string.product_details)
                .setView(mDialogView)
                .setPositiveButton(R.string.update, (dialog, id) -> {
                    setPantryProductChanges();
                    ((PantryActivity) mContext).getViewModel().updatePantryProduct(pantryProduct);
                })
                .setNegativeButton(R.string.cancel,  (dialog, id) ->
                        Objects.requireNonNull(PantryProductDetailsDialog.this.getDialog()).cancel());
        return alertDialogBuilder.create();
    }

    private void setPantryProductChanges() {
        NumberPicker availablePicker = mDialogView.findViewById(R.id.product_details_available);
        NumberPicker neededPicker = mDialogView.findViewById(R.id.product_details_needed);

        pantryProduct.setQttAvailable(availablePicker.getValue());
        pantryProduct.setQttNeeded(neededPicker.getValue());
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();

        assert dialog != null;
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        negativeButton.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        positiveButton.setTextColor(ContextCompat.getColor(mContext, R.color.black));

        setupDialogValues();
    }

    private void setupDialogValues() {
        TextView name = mDialogView.findViewById(R.id.product_details_name);
        TextView desc = mDialogView.findViewById(R.id.product_details_desc);
        ImageView image = mDialogView.findViewById(R.id.product_details_image);
        NumberPicker availablePicker = mDialogView.findViewById(R.id.product_details_available);
        NumberPicker neededPicker = mDialogView.findViewById(R.id.product_details_needed);
        availablePicker.setMinValue(0);
        neededPicker.setMinValue(0);
        availablePicker.setMaxValue(99);
        neededPicker.setMaxValue(99);
        name.setText(pantryProduct.getProduct().productName);
        desc.setText(pantryProduct.getProduct().productDescription);
        availablePicker.setValue(pantryProduct.getQttAvailable());
        neededPicker.setValue(pantryProduct.getQttNeeded());
        if(pantryProduct.getProduct().getImagePath() != null) {
            ((PantryActivity) mContext).getViewModel().getProductImage(pantryProduct.getProduct().getImagePath()).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(res ->
                    image.setImageBitmap(Bitmap.createScaledBitmap(res, res.getWidth(), res.getHeight(), false)));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
