package br.com.bossini.testeagendafirebase2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaDeContatosActivity extends AppCompatActivity {

    private ListView contatosListView;
    private ArrayAdapter <Contato> contatosAdapter;
    private List <Contato> contatos;

    //variáveis de instância
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference contatosReference;

    //inicializa
    private void configuraDatabase (){
        firebaseDatabase = FirebaseDatabase.getInstance();
        contatosReference = firebaseDatabase.getReference("contatos");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //no método onCreate
        contatosListView = findViewById(R.id.contatosListView);
        contatos = new ArrayList <>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intencaoAdicionarContato =
                        new Intent (ListaDeContatosActivity.this, AdicionaContatoActivity.class);
                startActivity(intencaoAdicionarContato);
            }
        });
        //no método onCreate
        configuraDatabase();
        contatos = new ArrayList <Contato>();
        contatosAdapter = new ArrayAdapter<Contato>(this, android.R.layout.simple_list_item_1, contatos);
        contatosListView.setAdapter(contatosAdapter);
        //no método onCreate, depois de instanciar a ListView
        configuraObserverLongClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //registra observador
        contatosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //exclui dados existentes anteriormente
                contatos.clear();
                //tratar dados recebidos aqui
                for (DataSnapshot json : dataSnapshot.getChildren()){
                    //conversão de JSON para objeto automática
                    Contato contato = json.getValue(Contato.class);
                    //configura a chave explicitamente
                    contato.setId(json.getKey());
                    contatos.add(contato);
                }
                //adapter notifica seus observadores (a ListView, neste caso)
                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //tratar eventuais erros aqui
                Toast.makeText(ListaDeContatosActivity.this, getString(R.string.erro_firebase), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configuraObserverLongClick (){
        contatosListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder dBuilder =
                        new AlertDialog.Builder(ListaDeContatosActivity.this);
                dBuilder.setPositiveButton(
                        getString(R.string.deletar_contato),
                        new DialogInterface.OnClickListener() {
                            //remoção
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Contato contato = contatos.get(position);
                                contatosReference.child(contato.getId()).removeValue();
                                Toast.makeText(ListaDeContatosActivity.this,
                                        getString(R.string.contato_removido),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton(
                                getString(R.string.atualizar_contato),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder dBuilder =
                                new AlertDialog.Builder(ListaDeContatosActivity.this);
                        View view = LayoutInflater.
                                from(ListaDeContatosActivity.this).
                                inflate(R.layout.activity_adiciona_contato, null);
                        final Contato contato = contatos.get(position);
                        final EditText nomeEditText =
                                view.findViewById(R.id.nomeEditText);
                        nomeEditText.setText(contato.getNome());
                        final EditText foneEditText =
                                view.findViewById(R.id.foneEditText);
                        foneEditText.setText(contato.getFone());
                        final EditText emailEditText =
                                view.findViewById(R.id.emailEditText);
                        emailEditText.setText(contato.getEmail());
                        final FloatingActionButton fab =
                                view.findViewById(R.id.fab);

                        final AlertDialog dialog = dBuilder.setView(view).create();
                        dialog.show();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String nome =
                                        nomeEditText.getEditableText().toString();
                                String fone =
                                        foneEditText.getEditableText().toString();
                                String email =
                                        emailEditText.getEditableText().toString();
                                contato.setNome(nome);
                                contato.setFone(fone);
                                contato.setEmail(email);
                                contatosReference.
                                        child(contato.getId()).
                                        setValue(contato);
                                Toast.makeText(ListaDeContatosActivity.this,
                                        getString(R.string.contato_atualizado), Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                }).create();
                dBuilder.show();
                return true;
            }
        });
    }
}
