var fileuploadControllers = angular
		.module(
				"fileuploadController",
				[],
				function($compileProvider) {
					$compileProvider
							.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
				});

fileuploadControllers.controller("FileuploadController", function($scope,
		$http, $filter, $routeParams) {

	$scope.setFiles = function(element) {
		$scope.$apply(function(scope) {
			console.log('files:', element.files);
			// Turn the FileList object into an Array
			$scope.files = []
			for (var i = 0; i < element.files.length; i++) {
				$scope.files.push(element.files[i])
			}
			$scope.progressVisible = false
		});
	};
	$scope.updateDatabase = function() {
		$http.get("/update");
	}
	$scope.uploadFile = function(myForm) {
		console.log(myForm);
		var fd = new FormData()
		for ( var i in $scope.files) {
			fd.append("uploadedFile", $scope.files[i])
		}
		fd.append("bank", myForm.bank);
		var xhr = new XMLHttpRequest();
//		xhr.upload.addEventListener("progress", uploadProgress, false)
//		xhr.addEventListener("load", uploadComplete, false)
//		xhr.addEventListener("error", uploadFailed, false)
//		xhr.addEventListener("abort", uploadCanceled, false)
		xhr.open("POST", "/fileupload");
//		scope.progressVisible = true
		xhr.send(fd);
	}
});