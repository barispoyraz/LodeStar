//
//  LoginViewController.swift
//  LodeStarApp
//
//  Created by Berk Abbasoglu on 17.01.2018.
//  Copyright © 2018 Berk Abbasoglu. All rights reserved.
//

import Foundation
import Alamofire

import UIKit

fileprivate let itemsPerRow: CGFloat = 1
fileprivate let sectionInsets = UIEdgeInsets(top: 6.0, left: 6.0, bottom: 6.0, right: 6.0)
fileprivate let cellCount = 10
fileprivate let reuseIdentifier = "landmarkCell"
fileprivate let key = "AIzaSyCnY6cljLR3vRDuCSdI0yOzDVyaLyfseRI"

fileprivate var landmarkNames = Array(repeating: "", count: 10)
fileprivate var landmarkAddresses = Array(repeating: "", count: 10)
fileprivate var landmarkRatings = Array(repeating: -1.0, count: 10)
fileprivate var landmarkData = Array(repeating: Data.init(), count: 10)

var landmarksArrElement:NSDictionary = ["": [""]]
var landmarksArr = Array(repeating: landmarksArrElement, count: 10)

extension LandmarksViewController {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return cellCount
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! LandmarkCell
        
        //background shadow for collectionView elements
        cell.layer.shadowColor = UIColor.black.cgColor
        cell.layer.shadowOffset = CGSize(width: 5, height: 5)
        cell.layer.shadowRadius = 5;
        cell.layer.shadowOpacity = 0.25;
        cell.clipsToBounds = false
        cell.layer.masksToBounds = false
        
        let index = indexPath as NSIndexPath
        
        if landmarkNames[index.row] != "" {
            
            cell.displayContent(landmarkPic: UIImage(data: landmarkData[index.row])!, name: landmarkNames[index.row].uppercased(), landmarkType: UIImage(named: "type.pdf")!, type: "Park", landmarkLocation: UIImage(named: "location.pdf")!, location: landmarkAddresses[index.row], star: Int(landmarkRatings[index.row]) + 1)
            
        }
        return cell
            
    }
}

extension LandmarksViewController {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        
        let paddingSpace = sectionInsets.left * (itemsPerRow + 1)
        let availableWidth = view.frame.width - paddingSpace
        let widthPerItem = availableWidth / itemsPerRow
        let heightperItem = 110 as CGFloat
        
        return CGSize(width: widthPerItem, height: heightperItem )
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return sectionInsets
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return sectionInsets.top
    }
}

class LandmarksViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    @IBOutlet weak var collectionView: UICollectionView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        jsonparseLandmarks(city: "Ankara")
        
        // Do any additional setup after loading the view,  from a nib.
        collectionView.dataSource = self
        collectionView.delegate = self
        
        self.collectionView.isScrollEnabled = true
        //self.navigationController?.navigationBar.isTranslucent = false
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func jsonparseLandmarks(city: String) {
        
        var requestTemplate = "http://lodestarapp.com:3010/?city=#city"
        
        requestTemplate = requestTemplate.replacingOccurrences(of: "#city", with: city)
        
        Alamofire.request(requestTemplate).responseJSON { response in
            if let json = response.result.value {
                
                let jsonInfoLandmarks = (json as? NSArray)!
                
                landmarksArr[0] = jsonInfoLandmarks[0] as! NSDictionary
                landmarksArr[1] = jsonInfoLandmarks[1] as! NSDictionary
                landmarksArr[2] = jsonInfoLandmarks[2] as! NSDictionary
                landmarksArr[3] = jsonInfoLandmarks[3] as! NSDictionary
                landmarksArr[4] = jsonInfoLandmarks[4] as! NSDictionary
                
                landmarksArr[5] = jsonInfoLandmarks[5] as! NSDictionary
                landmarksArr[6] = jsonInfoLandmarks[6] as! NSDictionary
                landmarksArr[7] = jsonInfoLandmarks[7] as! NSDictionary
                landmarksArr[8] = jsonInfoLandmarks[8] as! NSDictionary
                landmarksArr[9] = jsonInfoLandmarks[9] as! NSDictionary
                
                for i in 0..<10 {
                
                    let landmarkItem = landmarksArr[i]
                    
                    let landmarkName = landmarkItem["name"] as? String
                    landmarkNames[i] = landmarkName!
                    
                    let landmarkAddress = landmarkItem["formatted_address"] as? String
                    landmarkAddresses[i] = landmarkAddress!
                    
                    let landmarkRating = landmarkItem["rating"] as? Double
                    landmarkRatings[i] = landmarkRating!
                    
                    let landmarkPhoto = landmarkItem["photos"] as! NSArray
                    let landmarkPhotoDict = landmarkPhoto[0] as! NSDictionary
                    let landmarkPhotoRef = landmarkPhotoDict["photo_reference"] as? String
                    let maxheight = 100
                    
                    var urlTemplate = "https://maps.googleapis.com/maps/api/place/photo?photoreference=#ref&key=#key&maxheight=#maxheight"
                    urlTemplate = urlTemplate.replacingOccurrences(of: "#ref", with: (landmarkPhotoRef)!)
                    urlTemplate = urlTemplate.replacingOccurrences(of: "#key", with: key)
                    urlTemplate = urlTemplate.replacingOccurrences(of: "#maxheight", with: String(maxheight))
                    let url = URL(string: urlTemplate)
                    let data = try? Data(contentsOf: url!)
                
                    
                    
                   /* Alamofire.request(urlTemplate).responseJSON { response in
                        //print("Request: \(String(describing: response.request))")   // original url request
                        //print("Response: \(String(describing: response.response))") // http url response
                        //print("Result: \(response.result)")
                        
                        if let data = response.data, let utf8Text = String(data: data, encoding: .utf8) {
                         print("Data: \(utf8Text)") // original server data as UTF8 string
                         }
                    } */
                    
                    
                    
                    
                    
                    landmarkData[i] = data!
                    
                    self.collectionView.reloadData()
                    
                }
              //  self.collectionView.reloadData()
            }
        }
    }
    
    @IBAction func backTapAction(_ sender: UIButton) {
        dismiss(animated: true, completion: nil)
    }
    
    
}