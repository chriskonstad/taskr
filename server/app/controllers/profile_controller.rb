class ProfileController < ApplicationController


  skip_before_action :verify_authenticity_token

  # scope :android, -> {where(device_type: 'android')}

  # Get user profile information
  def show
    id = [params[:id]]
    user = User.find_by!(id: id)
    render :json => user.as_json({:methods => [:avgRating,
                                               :paid,
                                               :earned,
                                               :rated,
                                               :rating,
                                               :ratings,
                                               :request,
                                               :actions]})
  end

  # Allow the user to both sign in and login in, depending on if the user exists
  def login
    name = params[:name]
    email = params[:email]
    fbid = params[:fbid]


    user = User.login(name, email, fbid)
    
    if (!user.nil?)
      customer = Stripe::Customer.create(
      :email => params[:fbid]
       )
      render :json => { id: user.id, customer: customer}.to_json

    else
      render nothing: true, status: :bad_request if name.nil? || email.nil? || fbid.nil?
    end
    
    
  end


  # def notify(data, collapse_key = nil)

  #   fcm = FCM.new("AIzaSyAfgwTlcsudSPq5xh2BVCFcQ8I4z9j3nq8")
  #   registration_ids = params[:device_id]
  #   #registration_ids=["fill in the id here"] -> every device have different unique id
  #   #registration_ids = Device.android.map(&:registration_id) #an array of the client registration IDs
  #   options = {
  #     data: data,
  #     collapse_key: collapse_key || 'my_app'
  #   }
  #   response = fcm.send(registration_ids, options)

  # end


  def register_device
    uid = params[:id]
    device_id = params[:device_id]

    user = User.find_by(id: uid)

    if (!user.nil?)
      user.update(device_id: device_id)
      render nothing: true

    else
      render nothing:true, status: :bad_request if uid.nil? || device_id.nil?
    end
  end

end
