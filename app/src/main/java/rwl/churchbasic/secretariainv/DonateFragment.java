package rwl.churchbasic.secretariainv;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class DonateFragment extends Fragment {

    ImageButton buttonCopyPaste;
    TextView textQRCode;
    ImageView imageDonate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        imageDonate = view.findViewById(R.id.imageDonate);

        imageDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("INVBANC", "00020126360014BR.GOV.BCB.PIX0114131895020001545204000053039865802BR5925IGREJA DE NOVA VIDA EM SA6009SAO PAULO622605226OBfKUvsoAetlk21TMEtrL6304974D");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "PIX copiado para Area de Transferência", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}