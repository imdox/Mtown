package com.mtown.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.mtown.app.dao.ModelDAO;
import com.mtown.app.home.MainActivity;
import com.mtown.app.support.AppController;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private MyListViewAdapter adapter;
    private ArrayList<String> selectedIds;
    private ArrayList<String> selectedIdCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        selectedIds = new ArrayList<String>();
        selectedIdCode = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listview);

        // Pass value to MyListViewAdapter
         adapter = new  MyListViewAdapter(this, R.layout.list_item, MainActivity.modelDAOS);

        // Binds the Adapter to the ListView
        listView.setAdapter(adapter);

        // define Choice mode for multiple  delete
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.multiple_select, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                // TODO  Auto-generated method stub
                switch (item.getItemId()) {
                    case R.id.idDone:
                        selectedIds = new ArrayList<String>();
                        selectedIdCode = new ArrayList<String>();
                        AppController.modelIds = "";
                        AppController.modelCode = "";
                        // TODO  Auto-generated method stub
                        SparseBooleanArray selected = adapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                ModelDAO modelDAO = adapter.getItem(selected.keyAt(i));
                                selectedIds.add(modelDAO.getId());
                                selectedIdCode.add(modelDAO.getModel_code());
                            }
                        }
                        String ids = selectedIds.toString().replace("[","");
                        String code = selectedIdCode.toString().replace("[","");
                        AppController.modelIds = ids.replace("]","");
                        AppController.modelCode = code.replace("]","");
                        // Close CAB
                        mode.finish();
                        selected.clear();
                        finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // TODO  Auto-generated method stub
                final int checkedCount = listView.getCheckedItemCount();
                // Set the  CAB title according to total checked items
                mode.setTitle(checkedCount + "  Selected");
                // Calls  toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
            }
        });
    }
}