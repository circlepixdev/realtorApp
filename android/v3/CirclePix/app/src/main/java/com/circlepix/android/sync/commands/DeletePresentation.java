package com.circlepix.android.sync.commands;

import android.util.Log;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

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
		@GET("/deletePresentation.php")
		void doDelete(@Query("RealtorID") String realtorId,
				@Query("GUID") String guid,
				Callback<DeleteResponse> callback);
	}

	public static void runCommand(String realtorId, String guid, final Runnable postAction) {

		// Create REST adapter which points the getPresentationIds end point.
		DeletePresentationClient client = ServiceGenerator.createService(DeletePresentationClient.class, Constants.CIRCLEPIX_ENDPOINT);

		Callback<DeleteResponse> callback = new Callback<DeleteResponse>() {
			@Override
			public void success(DeleteResponse getPresentation,
					Response response) {
				Log.d("DeletePresentation", "Status: " + getPresentation.status
						+ " (Message: " + getPresentation.message + ")");

				if (postAction != null) {
					postAction.run();
				}



			}

			@Override
			public void failure(RetrofitError error) {
				ErrorHandler.log(error);
			}
		};

		// Fetch and process a presentation
		client.doDelete(realtorId, guid, callback);
	}

}
