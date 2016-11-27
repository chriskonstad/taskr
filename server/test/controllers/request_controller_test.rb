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

  test "should not get show" do 
  	assert_raises(ActionController::UrlGenerationError) do
    	get '/api/v1/request/show'
  	end
  end

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

  test "should not create when amount should be greater than 0" do 
  	post :create, request: {title: @samplecompleted.title, user_id: @samplecompleted.user_id, amount: -1, lat: @samplecompleted.lat, longitude: @samplecompleted.longitude, description: @samplecompleted.description}
  	assert_response 500
  end

#   test "nearby" do
#     get :nearby, {lat: @sampleopen.lat, longitude: @sampleopen.longitude, radius: 50}
#   #   post :cancel
#     assert_response 500

#   end

  test "nearby" do 
  	get :nearby, {lat: @sampleopen.lat, longitude: @sampleopen.longitude, radius: 50}
  	assert_response 200
  end

  test "can not run nearby without lat" do
  	get :nearby, {longitude: @sampleopen.longitude, radius: 40}
  	assert_response 400
  end

  test "can not run nearby without longitude" do
  	get :nearby, {lat: @sampleopen.lat, radius: 40}
  	assert_response 400
  end

  test "can not run nearby without radius" do 
  	get :nearby, {lat: @sampleopen.lat, longitude: @sampleopen.longitude}
  	assert_response 400
  end

  #fix
  # test "accept" do 
  # 	post :accept, {id: @sampleaccepted.id, user_id: @sampleaccepted.user_id}
  # 	assert_response 200
  # end

  # test "reject" do 
  # 	post :handle_action, {id: @sampleaccepted.id, user_id: @sampleaccepted.user_id}
  # 	assert_response 200
  # end

  # test "pay" do 
  # 	post :pay 
  # 	assert_response 200
  # end

  test "findByUid for requester and open" do
  	get :findByUid, {user_id: @sampleopen.user_id, role: 'requester'}
  	assert_response 200
  end

  test "findByUid for fulfiller and open" do
  	get :findByUid, {user_id: @sampleopen.user_id, role: 'fulfiller'}
  	assert_response 200
  end

  test "findByUid for requester and complete" do
  	get :findByUid, {user_id: @samplecompleted.user_id, role: 'requester'}
  	assert_response 200
  end

  test "findByUid for fulfiller and complete" do
  	get :findByUid, {user_id: @samplecompleted.user_id, role: 'fulfiller'}
  	assert_response 200
  end

  test "findByUid for requester and past due" do
  	get :findByUid, {user_id: @samplepastdue.user_id, role: 'requester'}
  	assert_response 200
  end

  test "findByUid for fulfiller and past due" do
  	get :findByUid, {user_id: @samplepastdue.user_id, role: 'fulfiller'}
  	assert_response 200
  end

  test "findByUid for requester and accept" do
  	get :findByUid, {user_id: @sampleaccepted.user_id, role: 'requester'}
  	assert_response 200
  end

  test "findByUid for fulfiller and accept" do
  	get :findByUid, {user_id: @sampleaccepted.user_id, role: 'fulfiller'}
  	assert_response 200
  end

  test "findByUid should work without role" do 
  	get :findByUid, {user_id: @sampleopen.user_id}
  	assert_response 200
  end


#   def format_something
#     "abc"
#   end
#   helper_method :format_something
# end
# test/functional/posts_controller_test.rb:

# require 'test_helper'

# class PostsControllerTest < ActionController::TestCase
#   test "the format_something helper returns 'abc'" do
#     assert_equal 'abc', @controller.send(:format_something)
#   end
# end



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
