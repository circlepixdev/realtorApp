package com.circlepix.android.sync.commands;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class DeletePresentation {

	/*
	 * Define the POJO structures that Gson will deserialize the server's JSON
	 * into
	 */
	// Be aware that the field names in these classes must exactly match the
	// names used in the JSON or ....
	// alternatively you can use the @SerializedName annotation if you want to
	// give them a different name.
	static class RealtorPresentation {
		String realtorId;
		String GUID;
	}

	static class Data {
		List<RealtorPresentation> RealtorPresentations;
	}

	static class DeleteResponse {
		String status;
		String message;
	}



	/* END OF deserialize POJO classes */

	interface DeletePresentationClient {
		@GET("deletePresentation.php")
		Call<DeleteResponse> doDelete(
				@Query("RealtorID") String realtorId,
				@Query("GUID") String guid
		);


	}

	public static void runCommand(String realtorId, String guid, final Runnable postAction) {

		// Create REST adapter which points the getPresentationIds end point.
		DeletePresentationClient client = ServiceGenerator.createService(DeletePresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

		Call<DeleteResponse> call = client.doDelete(realtorId, guid);

		call.enqueue(new Callback<DeleteResponse>() {
			 @Override
			 public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
				 if (response.isSuccessful()) {
					 Log.d("DeletePresentation", "Status: " + response.body().status
							 + " (Message: " + response.body().message + ")");

					 if (postAction != null) {
						 postAction.run();
					 }

				 } else {
					 Log.d("DeletePresentation", "Status: " + response.body().status
							 + " (Message: " + response.body().message + ")");

				 }
			 }

			 @Override
			 public void onFailure(Call<DeleteResponse> call, Throwable throwable) {
				 // if (throwable instanceof IOException){
				 //Add your code for displaying no network connection error
				// RetrofitException error = (RetrofitException) throwable;
				// ErrorHandler.log(error);
				 //  }
			 }
		});
	}

}
