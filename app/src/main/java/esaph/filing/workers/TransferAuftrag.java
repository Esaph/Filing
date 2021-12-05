package esaph.filing.workers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

import esaph.elib.esaphcommunicationservices.EsaphCommunicationClient;
import esaph.elib.esaphcommunicationservices.EsaphPipe;
import esaph.elib.esaphcommunicationservices.EsaphSocketCenter;
import esaph.elib.esaphcommunicationservices.Session;
import esaph.filing.CardsShowingFromList.Model.Auftrag.Auftrag;
import esaph.filing.R;
import esaph.filing.Utils.SocketConfig;

public class TransferAuftrag extends EsaphCommunicationClient
{
    private SoftReference<Context> contextWeakReference;
    private SoftReference<TransferAutragListener> transferAutragListenerWeakReference;
    private Auftrag auftragKarte;
    private long currentBoardListe;
    private long boardListeToTransfer;

    public TransferAuftrag(Context context,
                           Auftrag auftragKarte,
                           long currentBoardListe,
                           long boardListeToTransfer,
                           TransferAutragListener transferAutragListener)
    {
        this.contextWeakReference = new SoftReference<>(context);
        this.transferAutragListenerWeakReference = new SoftReference<>(transferAutragListener);
        this.auftragKarte = auftragKarte;
        this.currentBoardListe = currentBoardListe;
        this.boardListeToTransfer = boardListeToTransfer;
    }

    public interface TransferAutragListener
    {
        void onResult(Auftrag auftrag,
                      long boardListeCurrent,
                      long boardListeTransfered,
                      boolean success);
    }

    @Override
    public JSONObject bindData() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BIDC", currentBoardListe);
        jsonObject.put("BIDT", boardListeToTransfer);
        jsonObject.put("AID", auftragKarte.getmAuftragsId());
        return jsonObject;
    }

    @Override
    public EsaphSocketCenter.EsaphSocketCenterConfiguration requestSocketConfiguration() throws Exception
    {
        return SocketConfig.defaultConfig(contextWeakReference.get());
    }

    private boolean success = false;
    @Override
    public void onPipeReady(EsaphPipe esaphPipe)
    {
        try
        {
            JSONObject reply = new JSONObject(esaphPipe.getBufferedReader().readLine());
            success = reply.getBoolean("SUCCESS");
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "failed: " + ec);
        }
        finally
        {
            runUI(new Runnable()
            {
                @Override
                public void run()
                {
                    TransferAutragListener transferAutragListener = transferAutragListenerWeakReference.get();
                    if(transferAutragListener != null)
                    {
                        transferAutragListener.onResult(auftragKarte,
                                currentBoardListe,
                                boardListeToTransfer,
                                success);
                    }
                }
            });
        }
    }

    @Override
    public String getPipeCommand()
    {
        return "TA";
    }

    @Override
    public Session getSession()
    {
        return LoginWorkerSession.SessionHolder.getSession();
    }

    private ProgressDialog progressDialog;
    @Override
    public void onShowLoading()
    {
        Context context = contextWeakReference.get();
        if(context != null)
        {
            progressDialog = ProgressDialog.show(context,
                    context.getResources().getString(R.string.loadingTitle),
                    "", true);
            progressDialog.show();
        }
    }

    @Override
    public void onHideLoading()
    {
        if(progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    @Override
    public void onLibraryError(Exception e)
    {
        runUI(new Runnable()
        {
            @Override
            public void run() {
                TransferAutragListener transferAutragListener = transferAutragListenerWeakReference.get();
                if(transferAutragListener != null)
                {
                    transferAutragListener.onResult(auftragKarte,
                            currentBoardListe,
                            boardListeToTransfer,
                            false);
                }
            }
        });
    }
}
