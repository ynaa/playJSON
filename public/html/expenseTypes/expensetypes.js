var expTypeControllers = angular.module("expTypeControllers", [], function ($compileProvider) {
	  $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|javascript):/);
	});

expTypeControllers.controller("ExpenseTypesController", function($scope, $http) {
	$scope.expenseTypes = {};
	populateExpenseTypes($scope, $http);
	$scope.newExpType = {};
	$scope.add = function(newExpType) {
		var responsePromise = $http.post("/expenseTypes/add", newExpType, {});
		responsePromise.success(function(dataFromServer, status, headers, config) {
			populateExpenseTypes($scope, $http);
			$scope.newExpType.typeName = "";
		});
		responsePromise.error(function(data, status, headers, config) {
			alert("Legge til feilet!");
		});
	};
	$scope.edited = function(index) {
		 console.log("lagrer")
		var responsePromise = $http.post("/expenseTypes/edit/" + $scope.expenseTypes[index]._id, $scope.expenseTypes[index], {});
		responsePromise.error(function(data, status, headers, config) {
			alert("Editering feilet!");
		});
	};
	$scope.deleteExpType = function(index) {
		var expType = $scope.expenseTypes[index];
		if(confirm("Er du sikker på du vil slette  " + expType.typeName + "?")){
			var responsePromise = $http.delete("/expenseTypes/delete/" + expType._id, {});
			responsePromise.success(function(dataFromServer, status, headers, config) {
				$scope.expenseTypes.splice(index, 1);
			});
			responsePromise.error(function(data, status, headers, config) {
				alert("Sletting feilet!");
			});
		}
	}
});

expTypeControllers.controller("EditExpenseTypeController", function($scope, $http, $location, $routeParams) {
	$scope.editExpType = {};
	$http.get('/expenseTypes/edit/' + $routeParams.expTypeId).success(function(data, status, headers, config) {
		$scope.editExpType.typeName = data.expType.typeName;
		$scope.editExpType.id = data.expType._id;
	}).error(function(data, status, headers, config) {
		alert("Hente for edit feiler!");
	});
	$scope.edit = function(editExpType) {
		var responsePromise = $http.post("/expenseTypes/edit/" + editExpType.id, editExpType, {});
		responsePromise.success(function(dataFromServer, status, headers, config) {
			$location.path("#/list");
		});
		responsePromise.error(function(data, status, headers, config) {
			alert("Editering feilet!");
		});
	}
});

function populateExpenseTypes($scope, $http) {
	$http.get('/expenseTypes/list').success(function(data, status, headers, config) {
		$scope.expenseTypes = data.expDetList;
	});
}
