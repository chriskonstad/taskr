Rails.application.routes.draw do
  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  # root 'welcome#index'


  scope '/api' do
    scope '/v1' do
      scope '/test' do
        # Simple endpoint for testing the server
        get '/' => 'debug#hello'
      end
      # Debug scope for API calls not necessary for normal app function
      scope '/debug' do
        scope '/requests' do
          get '/' => 'debug#products'
          get '/user/:user_id' => 'debug#product'
        end
      end
      scope '/profile' do
        # Get user profile information by id lookup
        get '/:id' => 'profile#show'
        post '/' => 'profile#create'
      end
      scope '/requests' do
        get '/nearby' => 'request#nearby' # params: long, lat, radius (miles)
        post '/' => 'request#create'

        # TODO Need to add actor FK -> User on Request
        #post '/accept/:id' => 'request#accept'
        #post '/reject/:id' => 'request#reject'

        #post '/complete/:id' => 'request#complete'
        #post '/pay/:id' => 'request#pay'
        post '/cancel' => 'request#cancel'

        get '/:id' => 'request#show'
        post '/:id' => 'request#edit'
      end
    end
  end

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end

  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
