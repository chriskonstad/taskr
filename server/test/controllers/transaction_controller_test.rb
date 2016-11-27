require 'test_helper'

class TransactionControllerTest < ActionController::TestCase
  setup do
    @trans = transactions(:sampletrans)
  end

  test "should get show" do
    get :show, id: @trans
    assert_response :success
  end

  test "should create transaction" do
  	 post :create, transaction: {payer_id: @trans.payer_id, payee_id: @trans.payee_id, amount: @trans.amount, request_id: @trans.request_id}
  	 assert_response :success
  end

  test "should not create transaction without amount" do 
  	post :create, transaction: {payer_id: @trans.payer_id, payee_id: @trans.payee_id, request_id: @trans.request_id}
  	assert_response 500
  end

  test "should not create transation without payee" do 
  	 post :create, transaction: {payer_id: @trans.payer_id, amount: @trans.amount, request_id: @trans.request_id}
  	 assert_response 500
  end

  test "should not create transaction without payer" do 
  	post :create, transaction: { payee_id: @trans.payee_id, amount: @trans.amount, request_id: @trans.request_id}
  	assert_response 500
  end

  test "should not create transaction without request_id" do 
  	post :create, transaction: {payer_id: @trans.payer_id, payee_id: @trans.payee_id, amount: @trans.amount}
  	assert_response 500
  end
  

end
