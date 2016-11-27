require 'test_helper'

class RequestControllerTest < ActionController::TestCase

  setup do
  	@samplecompleted = requests(:samplecompleted)
  	@sampleopen = requests(:sampleopen)
  	@samplepastdue = requests(:openpastdue)
  	@sampleaccepted = requests(:sampleaccepted)
   
  end

  test "should get show sample completed" do
    get :show, id: @samplecompleted
    assert_response :success
  end

  test "should get show sample open" do 
  	get :show, id: @sampleopen
    assert_response :success
  end

  test "should get show sample past due" do 
  	get :show, id: @samplepastdue
    assert_response :success
  end

  test "should get show sample accepted" do 
  	get :show, id: @sampleaccepted
    assert_response :success
  end

  # test "should get edit sample open" do 
  # 	get :edit, id: @sampleopen
  #   assert_response :success
  # end


  # test "should get edit sample past due" do 
  # 	get :edit, id: @samplepastdue
  #   assert_response :success
  # end

  # test "should get edit sample accepted" do 
  # 	get :edit, id: @sampleaccepted
  #   assert_response :success
  # end 

  # test "should get edit sample completed" do 
  # 	get :edit, id: @samplecompleted
  #   assert_response :success
  # end

  test "should create requests" do
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: @samplecompleted.amount, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, due: @samplecompleted.due, description: @samplecompleted.description}
  	assert_response :success
  end

  test "should not create request without latitude" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: @samplecompleted.amount, longitude: @samplecompleted.longitude, due: @samplecompleted.due, description: @samplecompleted.description}
  	assert_response 500
  end

  test "should not create request without longitude" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: @samplecompleted.amount,lat: @samplecompleted.lat, due: @samplecompleted.due, description: @samplecompleted.description}
  	assert_response 500
  end

  test "should not create request without amount" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, due: @samplecompleted.due, description: @samplecompleted.description}
  	assert_response 500
  end

  test "should not create request without user" do 
  	post :create, request: {title: @samplecompleted.title, amount: @samplecompleted.amount, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, due: @samplecompleted.due, description: @samplecompleted.description}
  	assert_response 500
  end

  test "should not create request without due" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: @samplecompleted.amount, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, description: @samplecompleted.description}
  	assert_response 500
  end

  test "amount should be greater than 0" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: -1, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, description: @samplecompleted.description}
  	assert_response 500
  end



 # get '/nearby' => 'request#nearby' # params: longitude, lat, radius (miles)
 #        post '/' => 'request#create'

 #        post '/accept' => 'request#accept'
 #        post '/reject' => 'request#reject'

 #        post '/complete' => 'request#complete'
 #        post '/pay' => 'request#pay'
 #        post '/cancel' => 'request#cancel'

 #        get '/findByUid' => 'request#findByUid'

 #        get '/:id' => 'request#show'
 #        post '/:id' => 'request#edit'


end
