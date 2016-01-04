/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\project_studio\\MOWatch\\app\\src\\main\\aidl\\com\\mtk\\ipc\\IControllerInterface.aidl
 */
package com.mtk.ipc;
public interface IControllerInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mtk.ipc.IControllerInterface
{
private static final java.lang.String DESCRIPTOR = "com.mtk.ipc.IControllerInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mtk.ipc.IControllerInterface interface,
 * generating a proxy if needed.
 */
public static com.mtk.ipc.IControllerInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mtk.ipc.IControllerInterface))) {
return ((com.mtk.ipc.IControllerInterface)iin);
}
return new com.mtk.ipc.IControllerInterface.Stub.Proxy(obj);
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
case TRANSACTION_init:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
int _result = this.init(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendBytes:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
byte[] _arg2;
_arg2 = data.createByteArray();
int _arg3;
_arg3 = data.readInt();
long _result = this.sendBytes(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getConnectionState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getConnectionState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_close:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.close(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerControllerCallback:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mtk.ipc.IControllerCallback _arg1;
_arg1 = com.mtk.ipc.IControllerCallback.Stub.asInterface(data.readStrongBinder());
this.registerControllerCallback(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterControllerCallback:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mtk.ipc.IControllerCallback _arg1;
_arg1 = com.mtk.ipc.IControllerCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterControllerCallback(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getRemoteDeviceName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getRemoteDeviceName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mtk.ipc.IControllerInterface
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
     * Init IPCController.
     * @param cmd_type Only support Wearable SDK Controller CMD_8 or CMD_9
     * @param tagName Controller Tag
     */
@Override public int init(int cmd_type, java.lang.String tagName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd_type);
_data.writeString(tagName);
mRemote.transact(Stub.TRANSACTION_init, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Send bytes to Wearable.
     * @param tagName Controller Tag, use your IPCController init tag.
     * @param cmd command string, like "yahooweather yahooweather 1 0 0 "
     * @param dataBuffer byte type of data, like "".getBytes()
     * @param priority default PRIORITY_NORMAL, if set as PRIORITY_HIGH, this session
     *        will get top priority to send.
     */
@Override public long sendBytes(java.lang.String tagName, java.lang.String cmd, byte[] data, int priority) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tagName);
_data.writeString(cmd);
_data.writeByteArray(data);
_data.writeInt(priority);
mRemote.transact(Stub.TRANSACTION_sendBytes, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Return connection state.
     * 
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
@Override public int getConnectionState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getConnectionState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Destroy the IPCController.
     */
@Override public void close(java.lang.String tagName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tagName);
mRemote.transact(Stub.TRANSACTION_close, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * register IControllerCallback for the "tagName" IPCController.
     */
@Override public void registerControllerCallback(java.lang.String tagName, com.mtk.ipc.IControllerCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tagName);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerControllerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * unregister IControllerCallback for the "tagName" IPCController.
     */
@Override public void unregisterControllerCallback(java.lang.String tagName, com.mtk.ipc.IControllerCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tagName);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterControllerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * get SmartDevice APK remote Bluetooth device Name.
     */
@Override public java.lang.String getRemoteDeviceName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRemoteDeviceName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sendBytes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getConnectionState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_registerControllerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregisterControllerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getRemoteDeviceName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/**
     * Init IPCController.
     * @param cmd_type Only support Wearable SDK Controller CMD_8 or CMD_9
     * @param tagName Controller Tag
     */
public int init(int cmd_type, java.lang.String tagName) throws android.os.RemoteException;
/**
     * Send bytes to Wearable.
     * @param tagName Controller Tag, use your IPCController init tag.
     * @param cmd command string, like "yahooweather yahooweather 1 0 0 "
     * @param dataBuffer byte type of data, like "".getBytes()
     * @param priority default PRIORITY_NORMAL, if set as PRIORITY_HIGH, this session
     *        will get top priority to send.
     */
public long sendBytes(java.lang.String tagName, java.lang.String cmd, byte[] data, int priority) throws android.os.RemoteException;
/**
     * Return connection state.
     * 
     * @see WearableManager#STATE_NONE
     * @see WearableManager#STATE_LISTEN
     * @see WearableManager#STATE_CONNECT_FAIL
     * @see WearableManager#STATE_CONNECT_LOST
     * @see WearableManager#STATE_CONNECTING
     * @see WearableManager#STATE_CONNECTED
     * @see WearableManager#STATE_DISCONNECTING
     */
public int getConnectionState() throws android.os.RemoteException;
/**
     * Destroy the IPCController.
     */
public void close(java.lang.String tagName) throws android.os.RemoteException;
/**
     * register IControllerCallback for the "tagName" IPCController.
     */
public void registerControllerCallback(java.lang.String tagName, com.mtk.ipc.IControllerCallback callback) throws android.os.RemoteException;
/**
     * unregister IControllerCallback for the "tagName" IPCController.
     */
public void unregisterControllerCallback(java.lang.String tagName, com.mtk.ipc.IControllerCallback callback) throws android.os.RemoteException;
/**
     * get SmartDevice APK remote Bluetooth device Name.
     */
public java.lang.String getRemoteDeviceName() throws android.os.RemoteException;
}
