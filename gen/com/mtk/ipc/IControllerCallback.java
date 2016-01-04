/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\java\\MOWatch\\src\\com\\mtk\\ipc\\IControllerCallback.aidl
 */
package com.mtk.ipc;
public interface IControllerCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mtk.ipc.IControllerCallback
{
private static final java.lang.String DESCRIPTOR = "com.mtk.ipc.IControllerCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mtk.ipc.IControllerCallback interface,
 * generating a proxy if needed.
 */
public static com.mtk.ipc.IControllerCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mtk.ipc.IControllerCallback))) {
return ((com.mtk.ipc.IControllerCallback)iin);
}
return new com.mtk.ipc.IControllerCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onConnectionStateChange:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onConnectionStateChange(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onBytesReceived:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.onBytesReceived(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mtk.ipc.IControllerCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Notify WearableManager connection state.
     *
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
@Override public void onConnectionStateChange(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_onConnectionStateChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Notify WearableManager received data.
     */
@Override public void onBytesReceived(byte[] dataBuffer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(dataBuffer);
mRemote.transact(Stub.TRANSACTION_onBytesReceived, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onConnectionStateChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onBytesReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
     * Notify WearableManager connection state.
     *
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
public void onConnectionStateChange(int state) throws android.os.RemoteException;
/**
     * Notify WearableManager received data.
     */
public void onBytesReceived(byte[] dataBuffer) throws android.os.RemoteException;
}
