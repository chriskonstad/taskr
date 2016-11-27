require 'test_helper'

class DeviceControllerTest < ActionController::TestCase
  # test "the truth" do
  #   assert true
  # end

  # test "should create post" do
  #   assert_difference('Post.count') do
  #     post :create, post: { body: @post.body, title: @post.title }
  #   end

  #   assert_redirected_to post_path(assigns(:post))
  # end

  setup do
    @device = devices(:one)
  end

  test "should get show" do
    get :show, user_id: @device
    assert_response :success
    #assert_not_nil assigns(:devices)
  end

  test "should create device" do
  	post :create, device: {registration_id: @device.registration_id, device_type: @device.device_type}
  	assert_response :success
  end

  # test "should not create device without device type" do 
  # 	post :create, device: {registration_id: @device.registration_id}
  # 	assert_response 500
  # end

  # test "should not create device with duplicate registration_id" do 
  # 	post :create, device: {registration_id: @device.registration_id, device_type: @device_type}
  # 	assert_response 500
  # end




  # test "should get new" do
  #   get :new
  #   assert_response :success
  # end
end
