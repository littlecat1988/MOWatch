package care.utils;



import android.content.Context;
import android.content.SharedPreferences;

public class JfXmlTools {

	private Context cx;
	private static String shareXml = "axaet_share";
	public JfXmlTools(Context context){
		this.cx = context;
	}
	

	public void set_power_status(boolean power_status){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putBoolean("power_status",power_status);
		editor.commit();
	}

	public boolean get_power_status(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getBoolean("power_status",false);  //默认是false
	}
	public void set_username(String userName) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("userName", userName);
		editor.commit();
	}
	public String get_username(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("userName", "");
	}
	
	public void set_sid(String sid){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("sid", sid);
		editor.commit();
	}
	
	public String get_sid(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("sid", "");
	}
	
	public void set_imei(String imei){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("imei", imei);
		editor.commit();
	}
	
	public String get_imei(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("imei", "");
	}
	
	public void set_babyList(String babyList){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("baby_list", babyList);
		editor.commit();
	}
	
	public String get_babyList(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("baby_list", "");
	}
	
	public void set_circleList(String circleList){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("circle_list", circleList);
		editor.commit();
	}
	
	public String get_circleList(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("circle_list", "");
	}
	
	public void set_current_baby(String baby){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("current_baby", baby);
		editor.commit();
	}
	
	public String get_current_baby(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("current_baby", "");
	}
	
	public void set_current_baby_imei(String imei){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("current_baby_imei", imei);
		editor.commit();
	}
	
	public String get_current_baby_imei(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("current_baby_imei", "");
	}
	
		
	public void set_book(String book){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("book", book);
		editor.commit();
	}
	
	public String get_book(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("book", "");
	}
	
	
	
	public void set_smg_time(String smg_time){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("smg_time", smg_time);
		editor.commit();
	}
	
	public String get_smg_time(){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("smg_time", "");
	}
	
		public void set_baby_phone(String baby_phone) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("baby_phone", baby_phone);
		editor.commit();
	}

	public String get_baby_phone() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("baby_phone", "");
	}

	public void set_user_phone(String user_phone) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("user_phone", user_phone);
		editor.commit();
	}

	public String get_user_phone() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("user_phone", "");
	}

	public void set_frequency_number(String frequency_number) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("frequency_number", frequency_number);
		editor.commit();
	}
	
	
	public String get_user_password() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("user_password", "");
	}

	public void set_user_password(String user_password) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("user_password", user_password);
		editor.commit();
	}
	
	

	public String get_frequency_number() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("frequency_number", "0");
	}

	public void set_remenber_password(String remenber_password) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("remenber_password", remenber_password);
		editor.commit();
	}

	public String get_remenber_password() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("remenber_password", "0");
	}

	public void set_automatic_login(String automatic_login) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("automatic_login", automatic_login);
		editor.commit();
	}

	public String get_automatic_login() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("automatic_login", "0");
	}
	
	public void set_person(String person) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("person", person);
		editor.commit();
	}

	public String get_person() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("person", "");
	}
	
	public void set_baby_photo64(String baby_photo64) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("baby_photo64", baby_photo64);
		editor.commit();
	}

	public String get_baby_photo64() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("baby_photo64", "");
	}
	
	
	public void set_babylist_code(String babylist_code) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("babylist_code", babylist_code);
		editor.commit();
	}

	public String get_babylist_code() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("babylist_code", "0");
	}
	
	public void set_start_code(String start_code) {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("start_code", start_code);
		editor.commit();
	}

	public String get_start_code() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("start_code", "");
	}
	
	public void set_current_circle(String circle){
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		SharedPreferences.Editor editor = settin.edit();
		editor.putString("current_circle", circle);
		editor.commit();
	}
	
	public String get_current_circle() {
		SharedPreferences settin = cx.getSharedPreferences(shareXml, 0);
		return settin.getString("current_circle", "");
	}
	
	
}
