class DebugController < ApplicationController

  skip_before_action :verify_authenticity_token

  # Hello world endpoint
  def hello
    render :text => "hello world"
  end

  # Show ALL requests
  def products
    requests = Request.all
    render :json => requests.all
  end

  # Show all requests opened by user with ID user_id
  def product
    request = Request.where(user_id: [params[:user_id]])
    render :json => request.as_json
  end
end

