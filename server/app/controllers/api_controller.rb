class ApiController < ApplicationController

  skip_before_action :verify_authenticity_token

  def hello
    render :text => "hello world"
  end

  def profile
    email = [params[:email]]
    user = User.find_by!(email: email)
    render :json => user.as_json(methods: :avgRating)
  end

  # def createprofile
  #   user = User.new(api_user_params)

  #   if user.save
  #     render json: user 
  #   else
  #     render nothing: true, status: :bad_request
  #   end
  # end

  def products
    # TODO Filter based on location using Request.openNear
    requests = Request.all
    render :json => requests.all
  end

  def product
    request = Request.where(user_id: [params[:user_id]])
    render :json => request.as_json
  end

  private
    def api_user_params
      params.fetch(:user, {})
    end
end

